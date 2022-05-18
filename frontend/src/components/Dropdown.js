import React, {Component} from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCaretDown, faCaretUp } from "@fortawesome/free-solid-svg-icons";

/*
    Component for a simple dropdown menu
 */
class Dropdown extends Component {
    constructor() {
        super();
        this.state = {
            map: "",
            open: false,
        }
        this.changeDropdown = this.changeDropdown.bind(this)
        this.selectOption = this.selectOption.bind(this)
    }

    // Toggle whether the dropdown is open or not
    changeDropdown() {
        this.setState({open: !this.state.open})
    }

    // Selects an option from the dropdown
    selectOption(option) {
        this.setState({map: option})
        this.props.select(option);
        this.setState({open: false});
    }

    render() {

        return (
            <div className="dropdownContainer">
               <div>
                    <div className="dropdown-box">{this.state.map} 
                        <FontAwesomeIcon className="dropdownIcon-regular" icon={this.state.open ? faCaretUp : faCaretDown}
                            onClick={this.changeDropdown}/>
                    </div>
                </div>
                {this.state.open ?
                <div className="dropdownListContainer">
                    {this.props.options.map(option => 
                        <div key={option} className="dropdownOption" onClick={() => this.selectOption(option)}>{option}</div>)}
                </div> : <> </>}
            </div>
        );
    }
}

export default Dropdown;