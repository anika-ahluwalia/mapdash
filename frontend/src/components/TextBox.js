import React from 'react';

/*
    Component for an input text box where users can type in text.
 */
function TextBox(props) {

    return (
        <div>
            <input
                type="text"
                className="textBox"
                placeholder={props.placeholder}
                value={props.value}
                onChange={(event) => props.change(event.target.value)}
            />
        </div>
    );
}

export default TextBox;