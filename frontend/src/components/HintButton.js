import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLightbulb} from "@fortawesome/free-solid-svg-icons/faLightbulb";

/*
    Component for the hint button that a user clicks to orient themselves to the shortest path to their destination
 */
function HintButton(props) {

    return (
        <button className="hintButton" onClick={props.click} aria-label="Hint Button">
            <FontAwesomeIcon icon={faLightbulb}/>
        </button>
    );
}

export default HintButton;