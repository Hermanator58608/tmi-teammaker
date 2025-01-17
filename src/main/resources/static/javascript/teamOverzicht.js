function backButton()
{
    let urlParams = new URLSearchParams(window.location.search);
    const klascode = urlParams.get("klascode");
    window.location.href = "teamMaker.html?klascode=" + klascode;
}

function displayKlascode() {
    const urlParams = new URLSearchParams(window.location.search);
    const klascode = urlParams.get("klascode");
    document.getElementById("klascode").textContent = "Klascode: " + klascode;
}

async function getTeams() {

    const rawteams = await fetch('/teamOverzicht/teamArray');
    return await rawteams.json();
}

async function displayTeams() {
    let jsonTeamArray = await getTeams();
    createTeams(jsonTeamArray)
    disableLoader();
}

function createTeams(jsonTeamArray){
    var container = document.getElementById("team-indeling");

    for (let i = 0; i < jsonTeamArray.length; i++) {

        let obj = jsonTeamArray[i];
        if(obj.isCaptain)
        {
            let label = document.createElement("label");
            label.innerHTML = '<div class = "team" id="team' + obj.id + '">'
                + '<div class="captainLable" id="captain' + obj.id + '">' + "team: " + obj.id+ "<br>" + obj.naam +'</div>'
                + '<div class="memberLable" id="members' + obj.id + '">' + '</div>'  + '</div>'
                container.appendChild(label);
        }

        if(obj.isCaptain === false)
        {
            var containerLeerling = document.getElementById("members" + obj.id );
            let label = document.createElement("label");
            label.innerHTML = '<div class = "teamMembers">' + obj.naam + '</div>'
            containerLeerling.appendChild(label);

        }
    }
}


function disableLoader(){
    document.getElementById("loader").hidden = true;
}