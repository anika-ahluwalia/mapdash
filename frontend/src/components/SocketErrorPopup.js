import React from 'react';
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

/*
    Component for a popup warning message that displays when a web socket error occurs in the backend and
    the user has to reload their connection
 */
function SocketErrorPopup(props) {

    const className = props.inGame ? "socketErrorPopup inGame" : "socketErrorPopup"

    return (
        <div className={className}>
            <p>An error occurred with your server connection. Please reload the page and create a new user!</p>
            <FontAwesomeIcon className="errorCloseIcon" icon={faTimes} onClick={props.click}/>
        </div>
    );
}

export default SocketErrorPopup;