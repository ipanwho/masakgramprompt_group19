window.onload = async function() {
    const stats = await renderProcessingTimeChart();
    if (stats && stats.length > 0) {
        // Find the most recent experiment ID (if the backend supports it)
        const validStats = stats.filter(s => s.experimentId !== undefined);
        if (validStats.length > 0) {
            const latestExpId = Math.max(...validStats.map(s => s.experimentId));
        // Table removed, just render chart
    }
    }
};

async function runExperimentBatch() {

    const model = document.getElementById("modelSelect").value;
    const promptTechnique = document.getElementById("promptSelect").value;
    const statusText = document.getElementById("experimentStatus");
    const btn = document.getElementById("analyzeBtn");
    
    const progressContainer = document.getElementById("batchProgressContainer");
    const progressBar = document.getElementById("batchProgressBar");
    const progressText = document.getElementById("batchProgressText");

    // UI Loading state
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin me-2"></i> Running Batch...`;
    
    statusText.innerHTML = `<span class="status-indicator status-running"></span> Running...`;
    
    
    document.getElementById("statPending").innerText = "-";
    document.getElementById("statRunning").innerText = "-";
    document.getElementById("statCompleted").innerText = "-";
    document.getElementById("statFailed").innerText = "-";

    // Show Progress Bar
    progressContainer.classList.remove("d-none");
    progressText.classList.remove("d-none");
    progressBar.style.width = "0%";
    progressBar.innerText = "0%";
    progressText.innerText = "Initializing connection...";

    const eventSource = new EventSource(`/api/llm/analyze/batch?model=${model}&technique=${promptTechnique}`);

    eventSource.addEventListener("progress", (e) => {
        const data = JSON.parse(e.data);
        const percent = Math.round((data.current / data.total) * 100);
        
        progressBar.style.width = percent + "%";
        progressBar.innerText = percent + "%";
        progressText.innerHTML = `Processing <b>${data.current}</b> of <b>${data.total}</b> transcripts...<br><span class="text-secondary small">${data.currentFileName}</span>`;
        
        document.getElementById("statPending").innerText = data.pendingCount !== undefined ? data.pendingCount : "-";
        document.getElementById("statRunning").innerText = data.runningCount !== undefined ? data.runningCount : "-";
        document.getElementById("statCompleted").innerText = data.completedCount !== undefined ? data.completedCount : "-";
        document.getElementById("statFailed").innerText = data.failedCount !== undefined ? data.failedCount : "-";
    });

    eventSource.addEventListener("complete", async (e) => {
        const data = JSON.parse(e.data);
        eventSource.close();
        
        progressBar.style.width = "100%";
        progressBar.innerText = "100%";
        progressText.innerHTML = `<span class="text-success fw-bold"><i class="fa-solid fa-check-circle"></i> Successfully processed ${data.processedCount} transcripts!</span>`;
        
        statusText.innerHTML = `<span class="status-indicator status-completed"></span> Completed`;
        
        document.getElementById("statRunning").innerText = "0";
        document.getElementById("statPending").innerText = "0";
        if (data.processedCount !== undefined) document.getElementById("statCompleted").innerText = data.processedCount;
        if (data.failedCount !== undefined) document.getElementById("statFailed").innerText = data.failedCount;
        
        // Hide progress bar after a few seconds
        setTimeout(() => {
            progressContainer.classList.add("d-none");
            progressText.classList.add("d-none");
        }, 3000);

        // (Table removed, just render chart)
        
        // Update the chart to include the new data
        await renderProcessingTimeChart();
        
        btn.disabled = false;
        btn.innerHTML = `<i class="fa-solid fa-play"></i> Run Experiment Batch`;
    });

    eventSource.addEventListener("error", (e) => {
        eventSource.close();
        console.error("SSE Error:", e);
        
        // Sometimes the error object doesn't have data if it's a network disconnect
        let errMsg = "Connection lost or server error.";
        if (e.data) {
            try {
                const data = JSON.parse(e.data);
                errMsg = data.message || errMsg;
            } catch(err) {}
        }
        
        progressBar.classList.replace("bg-success", "bg-danger");
        progressText.innerHTML = `<span class="text-danger fw-bold"><i class="fa-solid fa-triangle-exclamation"></i> Stream Error: ${errMsg}</span>`;
        statusText.innerHTML = `<span class="status-indicator bg-danger"></span> Failed`;
        
        btn.disabled = false;
        btn.innerHTML = `<i class="fa-solid fa-rotate-right"></i> Retry Batch`;
    });
}


function renderGroundTruthTable(data) {
    const tbody = document.getElementById("groundTruthTableBody");
    if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-muted py-3">No data available</td></tr>`;
        return;
    }
    tbody.innerHTML = data.map(item => `
        <tr>
            <td class="text-start fw-medium">${item.nameOriginal || '-'}</td>
            <td class="text-muted">${item.nameEnglish || '-'}</td>
            <td><span class="badge bg-light text-dark border">${item.quantityValueCulinary || '-'}</span></td>
            <td><span class="badge bg-secondary">${item.quantityUnit || '-'}</span></td>
            <td class="text-primary fw-bold">${item.calories || '-'}</td>
        </tr>
    `).join('');
}

let processingChartInstance = null;

async function renderProcessingTimeChart() {
    try {
        const response = await fetch('/api/llm/stats/processing-time');
        if (!response.ok) return null;
        const stats = await response.json();
        
        if (stats.length === 0) return null;

        document.getElementById('chartContainer').classList.remove('d-none');
        
        const models = [...new Set(stats.map(s => s.modelName))];
        const techniques = [...new Set(stats.map(s => s.promptTechnique))];
        
        const colors = {
            "zero_shot": "#d63384",
            "few_shot": "#6f42c1",
            "structured_output": "#f06292",
            "chain_of_thought": "#9575cd"
        };
        const fallbackColors = ["#d63384", "#6f42c1", "#f06292", "#9575cd", "#20c997", "#fd7e14"];

        const datasets = techniques.map((technique, index) => {
            const data = models.map(model => {
                const stat = stats.find(s => s.modelName === model && s.promptTechnique === technique);
                return stat ? stat.avgProcessingTimeSeconds : 0;
            });
            
            return {
                label: technique.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()),
                data: data,
                backgroundColor: colors[technique] || fallbackColors[index % fallbackColors.length],
                borderWidth: 0,
                barPercentage: 0.8,
                categoryPercentage: 0.9,
                maxBarThickness: 50
            };
        });

        const ctx = document.getElementById('processingTimeChart').getContext('2d');
        
        if (processingChartInstance) {
            processingChartInstance.destroy();
        }
        
        processingChartInstance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: models,
                datasets: datasets
            },
            options: {
                indexAxis: 'y', // Horizontal bar chart
                responsive: true,
                maintainAspectRatio: false,
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            font: { size: 12 },
                            usePointStyle: true,
                            boxWidth: 8
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.dataset.label + ': ' + context.parsed.x.toFixed(1) + 's';
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Processing Time (seconds)',
                            font: { weight: 'bold' }
                        },
                        grid: { color: '#f0f0f0' }
                    },
                    y: {
                        grid: { display: false },
                        ticks: { font: { size: 11 } }
                    }
                }
            }
        });
        
        return stats;
    } catch (e) {
        console.error("Failed to render processing time chart:", e);
        return null;
    }
}

function downloadChart() {
    const canvas = document.getElementById("processingTimeChart");
    if (!canvas) return;
    
    // Create a temporary canvas with white background
    const newCanvas = document.createElement('canvas');
    newCanvas.width = canvas.width;
    newCanvas.height = canvas.height;
    const ctx = newCanvas.getContext('2d');
    
    // Fill white background
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, newCanvas.width, newCanvas.height);
    
    // Draw original chart over white background
    ctx.drawImage(canvas, 0, 0);
    
    // Trigger download
    const link = document.createElement('a');
    link.download = 'processing-time-chart.png';
    link.href = newCanvas.toDataURL('image/png');
    link.click();
}

async function loadDashboardReels() {
    const select = document.getElementById("reelSelect");
    select.innerHTML = '<option value="" selected disabled>Loading reels...</option>';
    
    try {
        const response = await fetch('/api/llm/dashboard/reels');
        const reels = await response.json();
        
        select.innerHTML = '<option value="" selected disabled>-- Select an Instagram Reel --</option>';
        reels.forEach(r => {
            select.innerHTML += `<option value="${r.reelId}">${r.instagramId} (@${r.influencerHandle})</option>`;
        });
        
        window.reelsData = reels;
    } catch (e) {
        console.error(e);
        select.innerHTML = '<option value="" selected disabled>Error loading reels</option>';
    }
}

async function loadReelDetails(reelId) {
    if (!reelId) return;
    
    const reelData = window.reelsData.find(r => r.reelId == reelId);
    if (reelData) {
        document.getElementById("reelDetailsCard").classList.remove("d-none");
        document.getElementById("detailReelId").innerText = reelData.instagramId;
        document.getElementById("detailInfluencer").innerText = "@" + reelData.influencerHandle;
        document.getElementById("detailDuration").innerText = reelData.duration + "s";
        document.getElementById("detailLanguage").innerText = reelData.language;
        
        let statusHtml = reelData.transcriptStatus === "Consistent" 
            ? `<span class="badge bg-success"><i class="fa-solid fa-check"></i> Consistent</span>`
            : `<span class="badge bg-warning text-dark"><i class="fa-solid fa-triangle-exclamation"></i> ${reelData.transcriptStatus}</span>`;
        document.getElementById("detailStatus").innerHTML = statusHtml;
        
        let gtHtml = reelData.groundTruthAvailable
            ? `<span class="badge bg-success"><i class="fa-solid fa-check"></i> Available</span>`
            : `<span class="badge bg-danger"><i class="fa-solid fa-xmark"></i> Missing</span>`;
        document.getElementById("detailGt").innerHTML = gtHtml;
    }
    
    document.getElementById("transcriptContent").innerHTML = '<div class="text-center mt-5"><div class="spinner-border text-primary" role="status"></div><div class="mt-2 text-muted">Loading transcript...</div></div>';
    document.getElementById("analysisTableBody").innerHTML = '<tr><td colspan="4" class="text-muted fst-italic py-4">Loading analysis data...</td></tr>';
    
    try {
        const response = await fetch(`/api/llm/dashboard/reel/${reelId}`);
        const data = await response.json();
        
        const highlighted = highlightCodeSwitching(data.transcriptText);
        document.getElementById("transcriptContent").innerHTML = highlighted.replace(/\n/g, '<br>');
        
        const tbody = document.getElementById("analysisTableBody");
        if (data.analysisList && data.analysisList.length > 0) {
            tbody.innerHTML = '';
            data.analysisList.forEach(a => {
                let statusBadge = a.jsonValid 
                    ? `<span class="badge bg-success">Success</span>`
                    : `<span class="badge bg-danger">Failed</span>`;
                
                tbody.innerHTML += `
                    <tr>
                        <td class="fw-bold">${a.model}</td>
                        <td>${a.technique}</td>
                        <td>${statusBadge}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="viewFactSheet(${a.resultId})"><i class="fa-solid fa-eye"></i> View</button>
                        </td>
                    </tr>
                `;
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="4" class="text-muted fst-italic py-4">No analysis data yet for this reel.</td></tr>';
        }
    } catch (e) {
        console.error(e);
        document.getElementById("transcriptContent").innerHTML = `<span class="text-danger">Failed to load details.</span>`;
    }
}

function highlightCodeSwitching(text) {
    const engWords = ["simple", "blender", "paste", "homemade", "caption", "confirm", "mix", "garlic", "chili", "oil", "onion", "blend", "ready", "done", "perfect", "taste", "try", "share", "like", "comment", "subscribe", "guys", "so", "and", "then", "okay", "ok", "next", "first", "lastly", "step", "easy", "quick", "fast", "food", "cook", "chef", "style"];
    const regex = new RegExp(`\\b(${engWords.join('|')})\\b`, 'gi');
    return text.replace(regex, match => `<mark class="bg-warning rounded px-1 fw-bold">${match}</mark>`);
}

async function viewFactSheet(resultId) {
    try {
        const response = await fetch(`/api/llm/nutrition/${resultId}`);
        const result = await response.json();
        
        let jsonStr = result.rawJsonOutput;
        try {
            jsonStr = JSON.stringify(JSON.parse(result.rawJsonOutput), null, 2);
        } catch(e) {}
        
        document.getElementById("factSheetContent").innerText = jsonStr;
        const modal = new bootstrap.Modal(document.getElementById('factSheetModal'));
        modal.show();
    } catch(e) {
        alert("Failed to load Fact Sheet.");
    }
}

let currentSortColumn = 'experiment.experimentId';
let currentSortDirection = 'desc';

function toggleSort(column) {
    if (currentSortColumn === column) {
        currentSortDirection = currentSortDirection === 'asc' ? 'desc' : 'asc';
    } else {
        currentSortColumn = column;
        currentSortDirection = 'asc';
    }
    loadExperimentData();
}

function updateSortIcons() {
    document.querySelectorAll('th.sortable i').forEach(icon => {
        icon.className = 'fa-solid fa-sort text-muted ms-1';
    });
    const activeHeader = document.querySelector(`th.sortable[onclick="toggleSort('${currentSortColumn}')"] i`);
    if (activeHeader) {
        activeHeader.className = currentSortDirection === 'asc' ? 'fa-solid fa-sort-up text-primary ms-1' : 'fa-solid fa-sort-down text-primary ms-1';
    }
}

async function loadExperimentData() {
    updateSortIcons();
    const model = document.getElementById("filterModel").value;
    const technique = document.getElementById("filterTechnique").value;
    const status = document.getElementById("filterStatus") ? document.getElementById("filterStatus").value : "All";
    const tbody = document.getElementById("dataResultsTableBody");

    if(!tbody) return;
    tbody.innerHTML = '<tr><td colspan="7" class="text-muted fst-italic py-4">Loading data...</td></tr>';

    try {
        const response = await fetch(`/api/llm/experiment-data?model=${model}&technique=${technique}&status=${status}&page=0&size=1000&sortBy=${currentSortColumn}&sortDir=${currentSortDirection}`);
        const data = await response.json();
        
        if (!data || !data.content || data.content.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-muted py-4">No results found.</td></tr>';
            return;
        }

        let html = "";
        data.content.forEach(res => {
            const exp = res.experiment || {};
            const expId = exp.experimentId ?? '-';
            const transId = exp.transcriptId ?? '-';

            const stat = exp.status ? exp.status.toUpperCase() : '-';
            
            // Format datetime if available
            let execAt = exp.executedAt ?? '-';
            if (execAt !== '-') {
                try {
                    const d = new Date(execAt);
                    execAt = d.toISOString().replace('T', ' ').substring(0, 19);
                } catch(err) {}
            }
            
            let mName = exp.modelName ?? '-';
            if (mName !== '-') {
                mName = mName.replace(/([a-zA-Z])(\d)/g, '$1 $2');
            }
            
            let pTech = exp.promptTechnique ?? '-';
            if (pTech !== '-') {
                pTech = pTech.replace(/_/g, ' ');
                pTech = pTech.charAt(0).toUpperCase() + pTech.slice(1).toLowerCase();
            }
            
            html += `
                <tr>
                    <td>${expId}</td>
                    <td>${transId}</td>
                    <td>${mName}</td>
                    <td>${pTech}</td>
                    <td>${stat}</td>
                    <td>${execAt}</td>
                    <td><a href="detail.html?experimentId=${expId}" class="btn btn-sm btn-outline-primary fw-bold"><i class="fa-solid fa-eye me-1"></i>View</a></td>
                </tr>
            `;
        });
        tbody.innerHTML = html;

    } catch(e) {
        console.error(e);
        tbody.innerHTML = `<tr><td colspan="7" class="text-danger py-4">Error loading data: ${e.message}</td></tr>`;
    }
}

async function viewDetails(resultId) {
    try {
        const response = await fetch(`/api/llm/nutrition/${resultId}`);
        const result = await response.json();
        
        let jsonStr = result.rawJsonOutput;
        try {
            // pretty print if it's valid json
            jsonStr = JSON.stringify(JSON.parse(jsonStr), null, 4);
        } catch(e) {}
        
        document.getElementById("factSheetContent").textContent = jsonStr;
        
        const modal = new bootstrap.Modal(document.getElementById('factSheetModal'));
        modal.show();
    } catch(e) {
        alert("Failed to load details.");
    }
}

function exportCSV() {
    const layerId = document.getElementById('exportSelect').value;
    if (!layerId) return;
    window.location.href = '/api/llm/export/' + layerId;
}

