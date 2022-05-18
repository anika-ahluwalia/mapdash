import React, {Component} from "react";

/*
    Component for toggling between two different options (used to toggle between Random and Find Friend modes)
 */
class ToggleBar extends Component {
    constructor() {
        super();
        this.state = {
        }
    }

    render() {

        return (
            <div className="toggleBar">
               <div className={this.props.value === this.props.option1 ? "toggleOption-select" : "toggleOption"}
                    onClick={() => this.props.changeValue(this.props.option1)}>{this.props.option1}</div>
               <div className={this.props.value === this.props.option2 ? "toggleOption-select" : "toggleOption"}
                    onClick={() => this.props.changeValue(this.props.option2)}>{this.props.option2}</div>
            </div>
        );
    }
}

export default ToggleBar;