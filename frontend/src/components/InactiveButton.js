import React from 'react';

/*
    Component for an inactive grayed out button, used to show that a user hasn't filled in a required form yet
 */
function InactiveButton(props) {

    return (
        <button className="inactiveButton" onClick={props.onClick ? 
            props.onClick : console.log()}>{props.label}</button>
    );
}

export default InactiveButton;