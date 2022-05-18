import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faMap} from "@fortawesome/free-solid-svg-icons";

/*
    Component for the map button that appears in the game screen. Displays a dynamic Google Map with the destination
    marked
 */
function DestinationMapButton(props) {

    return (
        <button className="destButton" onClick={() => props.click("map")} aria-label="Destination Map Button">
            <FontAwesomeIcon icon={faMap}/>
        </button>
    );
}

export default DestinationMapButton;