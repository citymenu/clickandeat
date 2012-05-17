function PostcodeAnywhere_Interactive_RetrieveByPostcodeAndBuilding_v1_10Begin(Postcode, Building) {

    var scriptTag = document.getElementById("PCA38d38252878f434581f85b249661cd94");
    var headTag = document.getElementsByTagName("head").item(0);
    var strUrl = "";

    //Build the url
    strUrl = "http://services.postcodeanywhere.co.uk/PostcodeAnywhere/Interactive/RetrieveByPostcodeAndBuilding/v1.10/json.ws?";
    strUrl += "&Key=" + 'ZZ23-WW99-YH92-EX79';
    strUrl += "&Postcode=" + escape(Postcode);
    strUrl += "&Building=" + escape(Building);
    strUrl += "&UserName=" + 'INDIV66124';
    strUrl += "&CallbackFunction=PostcodeAnywhere_Interactive_RetrieveByPostcodeAndBuilding_v1_10End";

    //Make the request
    if (scriptTag) {
        try {
            headTag.removeChild(scriptTag);
        }
        catch (e) {
            //Ignore
        }
    }

    scriptTag = document.createElement("script");
    scriptTag.src = strUrl
    scriptTag.type = "text/javascript";
    scriptTag.id = "PCA38d38252878f434581f85b249661cd94";
    headTag.appendChild(scriptTag);
}

function PostcodeAnywhere_Interactive_RetrieveByPostcodeAndBuilding_v1_10End(response) {

    //Test for an error
    if (response.length==1 && typeof(response[0].Error) != 'undefined') {
        //Show the error message
        alert(response[0].Description);
    } else {
        //Check if there were any items found
        if (response.length==0) {
            alert("Sorry, no matching items found");
        } else {
        	document.getElementById("address.address1").value= response[0].Line1;
        	document.getElementById("address.address2").value= response[0].Line2;
        	document.getElementById("address.address3").value= response[0].Line3;
        	document.getElementById("address.town").value= response[0].PostTown;
        	document.getElementById("address.region").value= response[0].County;
        }
    }
}