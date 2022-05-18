import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faImage} from "@fortawesome/free-solid-svg-icons";

/*
    Component for the picture button that appears on the game screen. Displays the first photo of the destination
    returned from the Google Places API
 */
function DestinationPicButton(props) {

    return (
        <button className="destButton" onClick={() => props.click("pic")} aria-label="Destination Picture Button">
            <FontAwesomeIcon icon={faImage}/>
        </button>
    );
}

export default DestinationPicButton;