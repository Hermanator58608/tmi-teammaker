//var kwaliteitnaam = document.getElementById("kwaliteit-naam");
//var kwaliteitOmschrijving = document.getElementById("kwaliteit-omschrijving");
//var kwaliteitid = document.getElementById("kwaliteit-id");
//Makes connection with Kwaliteitenblad.java and runs the method getKwaliteiten()

let maxchecked = 3;
let numChecked = 0;

async function get_kwaliteiten() {
    const urlParams = new URLSearchParams(window.location.search);
    const klascode = urlParams.get('klascode');
    const rawKwaliteiten = await fetch('/Kwaliteitenblad/Kwaliteiten?klascode=' +klascode);
    return await rawKwaliteiten.json();
}

async function display_kwaliteiten(klascode) {
    let jsonArrayKwaliteiten = await get_kwaliteiten();
    var container = document.getElementById("result-kwaliteiten");

    for (let i = 0; i < jsonArrayKwaliteiten.length; i++) {
        let obj = jsonArrayKwaliteiten[i];
        let label = document.createElement("label");

        label.innerHTML = '<div class = "kwaliteit">' + obj.Naam + '<br>' + obj.Omschrijving +
            '<input type="checkbox" name="' + obj.KwaliteitId + '" id="kwaliteitencheckbox-' + obj.KwaliteitId +
            '" onchange="checkboxChanged(this)"></div>';
        container.appendChild(label);
    }
    disableLoader();
}

function display_Checked_Kwaliteiten() {
    const uncheckedCheckboxes = document.querySelectorAll('input[type="checkbox"]:not(:checked)');
    const checkedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');

    uncheckedCheckboxes.forEach((checkbox) => {
        checkbox.parentNode.style.display = "none";
        checkbox.style.gap = "none";
        checkbox.parentNode.style.gap = "none";
    });

    checkedCheckboxes.forEach((checkbox) => {
       checkbox.style.display = "none";
       checkbox.parentNode.style.background = "";
        checkbox.parentNode.disabled = true;
        checkbox.disabled = true;
    });

    document.getElementById("terug").hidden = false;
    document.getElementById("bevestigen").hidden = false;
    document.getElementById("overzicht").hidden = true;
}

function undo_display_Checked_Kwaliteiten() {
    const uncheckedCheckboxes = document.querySelectorAll('input[type="checkbox"]:not(:checked)');
    const checkedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');

    uncheckedCheckboxes.forEach((checkbox) => {
        checkbox.parentNode.style.display = "";
    });

    checkedCheckboxes.forEach((checkbox) => {
        checkbox.style.display = "";
        checkbox.parentNode.style.background = "#008000";
        checkbox.parentNode.style.margin = "";
        checkbox.parentNode.disabled = false;
        checkbox.disabled = false;
    });

    document.getElementById("terug").hidden = true;
    document.getElementById("bevestigen").hidden = true;
    document.getElementById("overzicht").hidden = false;
}

async function setHeader(){
    const response = await fetch('/Kwaliteitenblad/setHeader');
    const rawHeader = await response.text();
    console.log(rawHeader)
    document.getElementById("header").innerText = rawHeader;
}

// define the checkboxChanged function to handle the checkbox change event
function checkboxChanged(checkbox) {
    if (checkbox.checked) {
        checkbox.parentNode.style.background = "#008000";
        numChecked++;
        if (numChecked >= maxchecked) {
            disableRemainingCheckboxes();
            document.getElementById("overzicht").disabled = false;

        }
    } else {
        checkbox.parentNode.style.background = "";
        numChecked--;
        if (numChecked < maxchecked) {
            enableAllCheckboxes();
            document.getElementById("overzicht").disabled = true;
        }
    }
}

function checkboxProjectChanged(checkbox) {
    if (checkbox.checked) {
        checkbox.parentNode.style.background = "#008000";
    } else {
        checkbox.parentNode.style.background = "";
    }
}

// function to disable remaining checkboxes
function disableRemainingCheckboxes() {
    let checkboxes = document.querySelectorAll("#result-kwaliteiten input[type='checkbox']:not(:checked)");
    for (let i = 0; i < checkboxes.length; i++) {
        checkboxes[i].disabled = true;
    }
}

// function to enable all checkboxes
function enableAllCheckboxes() {
    let checkboxes = document.querySelectorAll("#result-kwaliteiten input[type='checkbox']");
    for (let i = 0; i < checkboxes.length; i++) {
        checkboxes[i].disabled = false;
    }
}

// function to collect all selected checkboxes
function collectSelectedKwaliteitenCheckBoxes(){
    let checkboxeskwaliteiten = document.querySelectorAll("#result-kwaliteiten input[type='checkbox']:checked");
    let checkboxKwaliteitenIds = [];

    checkboxeskwaliteiten.forEach(function (checkbox) {
        let id = checkbox.getAttribute('id');
        let matches = id.match(/\d+/); // Extract the numbers from the id attribute
        if (matches) {
            checkboxKwaliteitenIds.push(matches[0]); // Add the first match to the checkboxIds array
        }
    })
    console.log(checkboxKwaliteitenIds);

    return checkboxKwaliteitenIds;
}

function collectSelectedProjectenCheckBoxes(){
    let checkboxesprojecten = document.querySelectorAll("#result-projecten input[type='checkbox']:checked");
    let checkboxProjectenIds = [];

    checkboxesprojecten.forEach(function (checkbox){
        let id = checkbox.getAttribute('id');
        let matches = id.match(/\d+/); // Extract the numbers from the id attribute
        if (matches) {
            checkboxProjectenIds.push(matches[0]); // Add the first match to the checkboxIds array
        }
    })
    console.log(checkboxProjectenIds);

    return checkboxProjectenIds;
}

async function get_post_Klascode() {
    const urlParams = new URLSearchParams(window.location.search);
    const klascode = urlParams.get('klascode');
    display_kwaliteiten();
    get_display_Projecten(klascode);
}

async function get_display_Projecten(klascode) {
    const rawProjecten = await fetch('/Kwaliteitenblad/Projecten?klascode=' +klascode);
    const jsonArrayProjecten = await  rawProjecten.json();

    var container = document.getElementById("result-projecten");

    for (let i = 0; i < jsonArrayProjecten.length; i++) {
        let obj = jsonArrayProjecten[i];
        let label = document.createElement("label");
        label.innerHTML = '<div class = "project">' + obj.Naam + '<input type= "checkbox" name="' + obj.projectid +
            '" id="projectencheckbox- ' + obj.projectid + ' " onchange="checkboxProjectChanged(this)"></div>';
        container.appendChild(label);
    }
    setHeader();
}

function bevestig_Kwaliteiten() {
    const urlParams = new URLSearchParams(window.location.search);
    const klascode = urlParams.get('klascode');
    const naam = urlParams.get('naam');
    const captain = urlParams.get('captain');
    const kwaliteiten_bevestigd =
    {
        naam : naam,
        klasid : klascode,
        kwaliteiten : collectSelectedKwaliteitenCheckBoxes(),
        projecten : collectSelectedProjectenCheckBoxes(),
        captain : captain
    };

    fetch('/Kwaliteitenblad/Bevestig', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(kwaliteiten_bevestigd)
    })
        .then(response => {     // Handle the response from the server
        if (response.ok) {      // Check if the response status is in the 200-299 range (indicating success)
            console.log('Data sent successfully!');
        }
    })
        .catch(error => {   // Handle any errors that occur during the fetch request
            console.error('Error sending data:', error);
        });

    hideButtons()
}

function hideButtons()
{
    document.getElementById("terug").hidden = true;
    document.getElementById("bevestigen").hidden = true;
    document.getElementById("overzicht").hidden = true;
}

function disableLoader(){
    document.getElementById("loader").hidden = true;
}