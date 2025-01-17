var docentbutton = document.getElementById("docent-button")
var captainButton = document.getElementById("captain-button");
var leerlingButton = document.getElementById("leerling-button")


// Add an event listener to the "docent-button" button
docentbutton.addEventListener("click", function() {
    window.location.href = "docentDashboard.html";
});

// Add an event listener to the "captain-button" button
captainButton.addEventListener("click", function() {
    postNaamAndKlascode(true)
});

// Add an event listener to the "leerling-button" button
leerlingButton.addEventListener("click", function() {
    postNaamAndKlascode(false)
});

function postNaamAndKlascode(boolValue) {
    var naam = document.getElementById("naam").value
    var klascode = document.getElementById("klascode").value

    // check if inputs are not empty
    if (naam && klascode) {
        // Create an options object for the fetch request
        const options = {
            // specify the HTTP method the request will use
            method: 'POST',
            // set the content-type to JSON format
            headers: {
                'Content-Type': 'application/json'
            },
            // define the data that will be sent to the server, in this case, a JSON string with 3 properties
            body: JSON.stringify({
                naam: naam,
                klascode: klascode,
                isCaptain: boolValue
            })
        };

        // Make a fetch request to the server
        fetch('/startpagina/student', options)
            // check if the response from the server is ok
            .then(response => {
                if (response.ok) {
                    // if the response is ok, return the json
                    return response.json();
                } else {
                    console.log('Error: ' + response.status);
                }
            })
            // get the json and check whether the 'exists' value is true or false
            .then(response => {
                const exists = response.exists;
                if (!exists) {
                    // if false, alert the user
                    alert("Klascode bestaat niet!")
                }
                else {
                    // if true, redirect the user to another page
                    window.location.href="kwaliteitenblad.html?klascode=" + klascode + "&naam=" + naam + "&captain=" + boolValue;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    } else {
        alert("Vul allebei de velden in!")
    }
}
