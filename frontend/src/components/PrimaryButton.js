import React from 'react';

/*
    Component for an active primary button that is used to open content, navigate between screens, etc.
 */
function PrimaryButton(props) {

    return (
        <button className="primaryButton" onClick={props.onClick ? 
            props.onClick : console.log()}>{props.label}</button>
    );
}

export default PrimaryButton;