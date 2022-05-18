import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowCircleUp} from "@fortawesome/free-solid-svg-icons/faArrowCircleUp";

/*
    Component for the popup that displays when a user uses a hint. Shows the direction that the user
    should proceed in for the shortest path to their destination
 */
function HintIndicator() {

    return (
        <div className="warningPopup">
            <h1 className="warningTitle">HINT</h1>
            <FontAwesomeIcon className="spinIcon" icon={faArrowCircleUp}/>
            <h2 className="warningSubtitle">This way!</h2>
        </div>
    );
}

export default HintIndicator;