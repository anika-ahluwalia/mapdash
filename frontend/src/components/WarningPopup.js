import React from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faSync} from "@fortawesome/free-solid-svg-icons/faSync";

/*
    Component for the popup that is displayed on the game screen when a player has been moving in the wrong direction
    for many consecutive moves and is still quite far away from the destination
 */
function WarningPopup() {

    return (
        <div className="warningPopup">
            <h1 className="warningTitle">WARNING</h1>
            <FontAwesomeIcon className="spinIcon" icon={faSync}/>
            <h2 className="warningSubtitle">You've been going the wrong way for a while!</h2>
        </div>
    );
}

export default WarningPopup;