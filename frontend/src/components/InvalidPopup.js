import React from 'react';

/*
    Component for a popup warning message that displays when a user enters a taken nickname or
    a has their game request denied
 */
function InvalidPopup(props) {

    return (
        <div className="invalidPopup" style={{display: props.visible ? "flex" : "none"}}>
            <p>{props.message}</p>
        </div>
    );
}

export default InvalidPopup;