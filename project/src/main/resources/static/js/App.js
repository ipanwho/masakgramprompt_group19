async function loadReelDetails() {
    const selector = document.getElementById("reelSelect");
    const metadata = document.getElementById("reelMetadata");
    const viewer = document.getElementById("transcriptViewer");

    if (selector.value === "1") {
        metadata.style.display = "table";
        viewer.innerHTML = `<span class="text-primary">${rawTranscriptText}</span>`;

        // Fetch ground truth from DB [cite: 84]
        try {
            const response = await fetch(`/api/llm/groundtruth/1`);
            if (response.ok) renderGroundTruthTable(await response.json());
        } catch (e) { console.error(e); }
    }
}

async function analyzeTranscript() {
    const model = document.getElementById("modelSelect").value;
    const statusBadge = document.getElementById("statusBadge");

    statusBadge.classList.remove("d-none");
    statusBadge.innerText = "Status: Running...";

    try {
        const response = await fetch(`/api/llm/analyze?model=${model}`, {
            method: "POST",
            body: rawTranscriptText
        });
        const jsonObj = await response.json();
        renderNutritionTable(jsonObj);
        statusBadge.innerText = "Status: Completed!";
    } catch (e) {
        statusBadge.innerText = "Status: Failed!";
    }
}

function renderGroundTruthTable(data) {
    const tbody = document.getElementById("groundTruthTableBody");
    tbody.innerHTML = data.map(item => `<tr><td>${item.nameOriginal}</td><td>${item.quantityValueCulinary}</td><td>${item.calories}</td></tr>`).join('');
}

function renderNutritionTable(jsonObj) {
    const tbody = document.getElementById("nutritionTableBody");
    tbody.innerHTML = jsonObj.ingredients.map(item => `<tr><td>${item.ingredient_name_original}</td><td>${item.quantity_value}</td><td>${item.calories}</td></tr>`).join('');
}