import React, {Component} from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCaretDown, faCaretUp } from "@fortawesome/free-solid-svg-icons";

/*
    Component for a dropdown menu containing different options and text filtering functionality
 */
class Dropdown extends Component {
    constructor() {
        super();
        this.state = {
            text: "",
            open: false,
            firstOpen: true
        }
        this.updateText = this.updateText.bind(this)
        this.changeDropdown = this.changeDropdown.bind(this)
        this.selectOption = this.selectOption.bind(this)
    }

    // Updates the input field's text value when user types
    updateText(value) {
        // Erase any selected map if text field is updated
        this.props.select("")
        this.setState({open: true});
        this.setState({text: value})
    }

    // Toggle whether the dropdown is open or not
    changeDropdown() {
        this.setState({open: !this.state.open})
    }

    // Determines whether an item in the dropdown matches search input
    inSearch(item) {
        if (item.toLowerCase().includes(this.state.text.toLowerCase())){
            return true;
        }
    }

    // Selects an option from the dropdown
    selectOption(option) {
        this.setState({text: option})
        this.props.select(option);
        this.setState({open: false});
    }

    // Opens dropdown list if it's the first click
    textClick() {
        if (this.state.firstOpen) {
            this.setState({open: true})
            this.setState({firstOpen: false})
        }
    }

    render() {

        return (
            <div className="dropdownContainer">
               <div>
                    <input
                        type="text"
                        className="dropdown-textBox"
                        placeholder={this.props.placeholder}
                        value={this.state.text}
                        onMouseDown={() => this.textClick()}
                        onChange={(event) => this.updateText(event.target.value)}
                    />
                    <FontAwesomeIcon className="dropdownIcon" icon={this.state.open ? faCaretUp : faCaretDown}
                        onClick={this.changeDropdown}/>
                </div>
                {this.state.open ?
                <div className="dropdownListContainer">
                    {this.props.options.filter(item => this.inSearch(item)).map(option => 
                        <div key={option} className="dropdownOption" onClick={() => this.selectOption(option)}>{option}</div>)}
                </div> : <> </>}
            </div>
        );
    }
}

export default Dropdown;