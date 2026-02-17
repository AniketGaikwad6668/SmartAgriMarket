let chart = null;   // IMPORTANT

async function loadData() {

    const crop = document.getElementById("crop").value;
    if (!crop) {
        alert("Select crop");
        return;
    }

    try {

        const prices = await fetch(`/api/prices?crop=${crop}`).then(r => r.json());
        const best = await fetch(`/api/recommend?crop=${crop}`).then(r => r.json());
        const prediction = await fetch(`/api/predict?crop=${crop}`).then(r => r.json());
        const weekly = await fetch(`/api/weekly?crop=${crop}`).then(r => r.json());

        document.getElementById("recommendation").innerHTML =
            `📍 Best Market: <b>${best.market}</b> (₹${best.modal})`;

        document.getElementById("prediction").innerHTML =
            `📈 Next Week Price: <b>₹${prediction.predictedPrice}</b>`;

        let avg = weekly.reduce((s,d)=>s+d.price,0)/weekly.length;

        let trend = weekly[6].price > weekly[0].price ? 
            "📈 Prices Increasing — Good Time to Wait" :
            "📉 Prices Decreasing — Consider Selling Now";

        document.getElementById("trend").innerHTML =
            `📊 Weekly Average: ₹${Math.round(avg)} <br>${trend}`;

        let table = "<table><tr><th>Market</th><th>Price</th></tr>";
        prices.forEach(p=>{
            table += `<tr class="${p.market===best.market?'highlight':''}">
                        <td>${p.market}</td>
                        <td>₹${p.modal}</td>
                      </tr>`;
        });
        table += "</table>";
        document.getElementById("table").innerHTML = table;

        // Destroy old chart safely
        if (chart instanceof Chart) {
            chart.destroy();
        }

        chart = new Chart(document.getElementById("chart"), {
            type: 'line',
            data: {
                labels: weekly.map(d => d.day),
                datasets: [{
                    label: 'Last 7 Days Trend',
                    data: weekly.map(d => d.price),
                    borderWidth: 4,
                    borderColor: '#00e676',
                    backgroundColor: 'rgba(0,230,118,0.3)',
                    fill: true,
                    pointRadius: 0   // no dots (farmer friendly)
                }]
            },
            options: {
                interaction: {
                    mode: 'index',
                    intersect: false
                },
                plugins: {
                    legend: {
                        labels: { font: { size: 18 } }
                    }
                },
                scales: {
                    x: { ticks: { font: { size: 16 } } },
                    y: { ticks: { font: { size: 16 } } }
                }
            }
        });

    } catch (error) {
        console.error(error);
        alert("Error loading data");
    }
}
