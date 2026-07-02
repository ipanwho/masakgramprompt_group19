const fs = require('fs');
const mysql = require('mysql2/promise');
const path = require('path');

async function exportCSV() {
    console.log("Reading SQL file...");
    const sqlFile = fs.readFileSync('src/main/resources/static/db/metrics_evaluation_queries.sql', 'utf8');
    
    // Connect to database
    console.log("Connecting to database...");
    const connection = await mysql.createConnection({
        host: 'switchback.proxy.rlwy.net',
        port: 21028,
        user: 'root',
        password: 'PXDRomZJbmCRXJBOMWKVuSawxmDFVvsY',
        database: 'masakgramprompt'
    });
    console.log("Connected successfully.");

    // Match each output file and its corresponding query
    const regex = /OUTPUT FILE\s*:\s*(\S+\.csv)[\s\S]*?(?:-- NOTE: Uncomment.*?\n)?[\s\S]*?(SELECT[\s\S]+?;)/gi;
    
    let match;
    while ((match = regex.exec(sqlFile)) !== null) {
        const fileName = match[1];
        const rawQuery = match[2];
        
        // Skip commented queries (like layer 4)
        if (rawQuery.trim().startsWith('--') || fileName === 'layer4_human_evaluation.csv') {
            console.log(`Skipping ${fileName} as its query is commented out...`);
            continue;
        }

        console.log(`Running query for ${fileName}...`);
        try {
            const [rows, fields] = await connection.query(rawQuery);
            
            if (rows.length === 0) {
                console.log(`No data returned for ${fileName}`);
                continue;
            }
            
            // Format as CSV
            const headers = fields.map(f => f.name).join(',');
            
            const csvRows = rows.map(row => {
                return fields.map(f => {
                    let val = row[f.name];
                    if (val === null || val === undefined) return '';
                    if (typeof val === 'string') {
                        // Replace newlines and escape quotes
                        val = val.replace(/\r?\n/g, ' ');
                        return `"${val.replace(/"/g, '""')}"`;
                    }
                    return val;
                }).join(',');
            });
            
            const csvContent = headers + '\n' + csvRows.join('\n');
            fs.writeFileSync(fileName, csvContent);
            console.log(`Successfully exported ${fileName} with ${rows.length} rows.`);
            
        } catch (err) {
            console.error(`Failed to execute query for ${fileName}:`, err.message);
        }
    }

    await connection.end();
    console.log("All done!");
}

exportCSV();
