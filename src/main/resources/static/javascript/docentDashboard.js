// Define an empty array to hold the list items
const maxProjects = 10;
let projects = [];
let klascode;
let bladid;

// Function to add the item to the list
function addItem() {
    if (projects.length < maxProjects) {
        // Get the input element
        let input = document.getElementById("projectInput");

        // Add the item to the array
        if (input.value !== "") {
            projects.push(input.value);
        }

        // Clear the input field
        input.value = "";

        // Update the label with the list items
        updateLabel();
    }
}

// Function to update the label with the list items
function updateLabel() {
    // Get the label element
    let label = document.getElementById("projectList");

    // Create a string with the list items
    let itemList = "";
    for(let i = 0; i < projects.length; i++) {
        itemList += "<li>" + projects[i] + "</li>";
    }

    // Set the label HTML with the list items
    label.innerHTML = "<ul>" + itemList + "</ul>";
}

function confirm() {
    // Create an object containing the data to send in the request body
    const confirmData = {
        "projects": projects,
        "klascode": klascode,
    };

    // Send a POST request to the specified URL with the request body as JSON
    fetch('/docent_dashboard/confirm', {
        // specify the HTTP method the request will use
        method: 'POST',
        // set the content-type to JSON format
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(confirmData)   // Convert the confirmData object to a JSON string and set it as the request body
    })
        .then(response => {     // Handle the response from the server
            if (response.ok) {      // Check if the response status is in the 200-299 range (indicating success)
                console.log('Data sent successfully!');
            }
        })
        .catch(error => {   // Handle any errors that occur during the fetch request
            console.error('Error sending data:', error);
        });

    const confirmbladdata = {
        klascode: klascode,
        bladid: bladid
    };

    fetch('/docent_dashboard/confirmBladid', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(confirmbladdata)
    })
        .then(response => response.json())
        .then(responseData => {
            // Process the response from the Java server
            console.log(responseData);
        })
        .catch(error => {
            // Handle any errors
            console.error('Error:', error);
        });

    // redirect user to a different page, also sending the klascode variable with it
    window.location.href = "teamMaker.html?klascode=" + klascode;
}

function fetchKlascode() {
    // Make the fetch request
    return fetch('/docent_dashboard/generate_klascode')
        .then(response => response.json())  // Handle the response from the server
        .then(data => {
            klascode = data;
            return klascode;
        })
        .catch(error => {   // Handle any errors that occur during the fetch request
            console.error(error);
        });
}

/* When the user clicks on the button,
 toggle between hiding and showing the dropdown content */
function showHideDropdown() {
    let dropdownContent = document.getElementById("myDropdown");
    dropdownContent.classList.toggle("show");


// Close the dropdown menu if the user clicks outside of it
    window.onclick = function (event) {
        if (!event.target.matches('.dropbtn')) {
            let dropdowns = document.getElementsByClassName("dropdown-content");
            for (let i = 0; i < dropdowns.length; i++) {
                let openDropdown = dropdowns[i];
                if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                }
            }
        }
    };
}
async function loadKwaliteitenbladen() {
    const rawkwaliteitenbladeren = await fetch('/docent_dashboard/get_kwaliteitenbladen');
    return await rawkwaliteitenbladeren.json();
}

async function display_kwaliteitenbladen() {
    let jsonArrayKwaliteitenBladen = await loadKwaliteitenbladen();
    let container = document.getElementById("myDropdown");

    for (let i = 0; i < jsonArrayKwaliteitenBladen.length; i++) {
        let obj = jsonArrayKwaliteitenBladen[i];
        let label = document.createElement("div");
        label.classList.add("kwaliteit");
        label.textContent = obj.naam;
        label.id = obj.bladId;
        label.addEventListener("click", function() {
            setBladId(label.id);
            console.log(label.id);
        });
        container.appendChild(label);
    }
}

function setBladId(bladId){
    bladid = bladId;
}