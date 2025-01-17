let teamCount = 0;
let currentDisplayedTeamID = 1;
let leerlingCount = 0;


function displayKlascode() {
    // get a reference to the klascode label
    const klascodeLabel = document.getElementById('klascode');

    // set the klascode label text
    klascodeLabel.textContent = "Klas code: " + getKlascode()
}

function initWebSockets() {
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    // Connect to the WebSocket server
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Subscribe to the "/topic/captainData/{klasId}" topic
        stompClient.subscribe('/topic/captainData/' + getKlascode(), async function (captain) {
            console.log('Received captain data:', captain.body);
            const captainData = JSON.parse(captain.body);
            await createTeamLabel(captainData)
            createTeamButton()
        });

        // Subscribe to the "/topic/leerlingData/{klasId}" topic
        stompClient.subscribe('/topic/leerlingData/' + getKlascode(), async function (leerling) {
            console.log('Received leerling data:', leerling.body);
            const leerlingData = JSON.parse(leerling.body);
            leerlingCount++;
            // Do something with the received data
            await createLeerlingLabel(leerlingData);

        });
    });
}

async function createLeerlingLabel(leerling) {
    let container = document.getElementById("studenten");
    let label = document.createElement('label');
    label.className = "student-label"
    label.setAttribute('name', 'leerlingLabel');
    label.setAttribute('naam', leerling.naam);
    label.setAttribute('leerlingId', leerling.leerlingId);
    label.setAttribute('hasTeam', 'no');
    label.setAttribute('teamId', '0');
    label.id = leerlingCount.toString();
    label.textContent = getKwaliteiten(leerling.kwaliteitNamen);
    label.onclick = function () {
        studentLabelClicked(this)
    };
    container.appendChild(label);
}

function getKwaliteiten(kwaliteiten) {
    let kwaliteitenString = "";
    for (const key in kwaliteiten) {
        kwaliteitenString += kwaliteiten[key] + "\n ";
    }
    return kwaliteitenString;
}

async function createTeamLabel(captain) {
    teamCount++;
    const container = document.getElementById("team-header")
    let label = document.createElement("label");

    label.id = teamCount.toString();
    label.setAttribute('name', 'teamLabel');
    label.setAttribute('naam' , captain.naam);
    label.className = 'student-label'
    label.textContent = captain.naam + '\n' + getKwaliteiten(captain.kwaliteitNamen);

    if (label.getAttribute('id') !== currentDisplayedTeamID.toString()) {
        label.style.display = 'none';
    }

    container.appendChild(label);
}

function getKlascode() {
    // get the klascode variable from the url
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('klascode')
}

function createTeamButton() {
    let button = document.createElement("button");
    const container = document.getElementById("teams");
    button.id = teamCount.toString();
    button.name = "teamButton";
    button.textContent = teamCount.toString();
    button.onclick = function() { teamButtonClicked(this) };
    container.appendChild(button);
}

function teamButtonClicked(button) {
    let header = document.getElementById("teamHeader")
    header.textContent = "Team: " + button.id
    currentDisplayedTeamID = button.id;
    changeTeamLabel()
}

function changeTeamLabel() {
    // get all elements with the same name
    const elements = document.querySelectorAll('[name="teamLabel"]');
    const leerlingElements = document.querySelectorAll('[name="leerlingLabel"]');

    // loop through each element and disable it, except for the one with the specified id
    for (let i = 0; i < elements.length; i++) {
        if (elements[i].getAttribute('id') !== currentDisplayedTeamID.toString()) {
            elements[i].style.display = 'none';
        } else {
            elements[i].style.display = '';
        }
    }

    // loop through each leerlingElement and disable it if it has been set to a team, except for the one with the specified id
    for (let i = 0; i < leerlingElements.length; i++)
    {
        if(leerlingElements[i].getAttribute('hasTeam') === "yes")
        {
            if(leerlingElements[i].getAttribute('teamId') !== currentDisplayedTeamID.toString()){
                leerlingElements[i].style.display = 'none';
            }
            else {
                leerlingElements[i].style.display = '';
            }
        }
        else {
            leerlingElements[i].style.display = '';
        }
    }
}


//if student label gets clicked this function runs
function studentLabelClicked(label) {
    //get both containers from team and studenten
    const teamContainer = document.getElementById("team");
    const leerlingContainer = document.getElementById("studenten");
    console.log("clicked label: " + label.textContent);

    //Looks if the "hasTeam" variable is equal to "no"
    if(label.getAttribute('hasTeam') === "no"){
        //sets "hasTeam" to "yes" for the selected label
        label.setAttribute('hasTeam',"yes");
        //sets "teamId" to the currentDisplayedTeamId for the selected label
        label.setAttribute('teamId', currentDisplayedTeamID.toString());
        setLeerlingenTeam(currentDisplayedTeamID, label.leerlingId);
        //transfers the selected label to the team container
        teamContainer.appendChild(label);
    }
    else {
        //sets "hasTeam" to "no" for the selected label
        label.setAttribute('hasTeam', "no");
        //SETS "teamId" to 0
        label.setAttribute('teamId', "0");
        //transfers the selected label to the studenten container
        leerlingContainer.appendChild(label);
    }

    console.log(label.getAttribute('hasTeam'));
    console.log(label.getAttribute('teamId'));
}
function setLeerlingenTeam(teamId, leerlingId) {

}

async function toonNamen() {
    const elements = document.querySelectorAll('[name="teamLabel"]');
    const leerlingElements = document.querySelectorAll('[name="leerlingLabel"]');

    var klasArray = [];

    for (let i = 0; i < elements.length; i++) {
        var leerling = new Leerling(true,elements[i].getAttribute("id"),elements[i].getAttribute("naam"));
        klasArray.push(leerling);
    }

    for (let i = 0; i < leerlingElements.length; i++){
        var leerling = new Leerling(false,leerlingElements[i].getAttribute("teamid"),leerlingElements[i].getAttribute("naam"));
        klasArray.push(leerling);
    }

    await sendKlasArray(klasArray);
    window.location.href = "teamOverzicht.html?klascode=" + getKlascode();
}

async function sendKlasArray(klassArray) {
    try {
        await fetch('/teamOverzicht/klasArray', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(klassArray)
        });
    } catch (error) {
        console.error('Error:', error);
    }
}



function Leerling(isCaptain, id, naam) {
    this.isCaptain = isCaptain;
    this.id = id;
    this.naam = naam;
}

async function getLeerlingenFromDB() {
    const rawLeerlingen = await fetch('/teamMaker/leerlingen?klascode=' + getKlascode());
    const jsonArrayLeerlingen = await rawLeerlingen.json();

    for (let i = 0; i < jsonArrayLeerlingen.length; i++) {
        let leerling = jsonArrayLeerlingen[i];
        if (leerling.captain) {
            await createTeamLabel(leerling)
            createTeamButton();
        } else {
            await createLeerlingLabel(leerling)
        }
    }

    let header = document.getElementById("teamHeader")
    header.textContent = "Team: 1"
    currentDisplayedTeamID = 1;
    changeTeamLabel();
    disableLoader();
}

function showLeerlingDetails(leerlingId) {
    // Get the leerling's details from the server
    get_leerling_details(leerlingId).then(function(leerlingDetails) {
        // Display the leerling's details
        // For example:
        const detailsContainer = document.getElementById('leerling-details');
        detailsContainer.innerHTML = '<p>Naam: ' + leerlingDetails.Naam + '</p>';
    }).catch(function(error) {
        console.error('Error retrieving leerling details:', error);
        alert('Error retrieving leerling details:\n' + error.message);
    });
}

function disableLoader(){
    document.getElementById("loader").hidden = true;
}