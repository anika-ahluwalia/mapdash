import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome} from "@fortawesome/free-solid-svg-icons";

/*
    Component for the return to start button that sends the user back to the starting location they were
    originally given
 */
function ReturnToStartButton(props) {

    return (
        <button className="destButton" onClick={props.click} aria-label="Return to Start Button">
            <FontAwesomeIcon icon={faHome}/>
        </button>
    );
}

export default ReturnToStartButton;