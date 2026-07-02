const http = require('http');

http.get('http://localhost:8080/api/llm/experiment-data?model=All&technique=All&status=All&page=0&size=1000&sortBy=resultId&sortDir=desc', (res) => {
  let data = '';
  res.on('data', (chunk) => {
    data += chunk;
  });
  res.on('end', () => {
    try {
      const json = JSON.parse(data);
      console.log("JSON parsed successfully. Content length:", json.content ? json.content.length : "undefined");
      
      // Simulate app.js logic
      json.content.forEach(res => {
         const techStr = res.experiment && res.experiment.promptTechnique ? res.experiment.promptTechnique.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()) : '-';
      });
      console.log("No errors during processing!");
    } catch(e) {
      console.error("Error:", e.message);
    }
  });
}).on("error", (err) => {
  console.log("Error: " + err.message);
});
