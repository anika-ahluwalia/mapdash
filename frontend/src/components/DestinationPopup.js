import React, {useContext, useEffect, useState} from 'react';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import UserContext from "../components/UserContext";

/*
    Component for the popup that displays either the dynamic map with the destination or a picture of the
    destination depending on which button was clicked in the game screen.
 */
function DestinationPopup(props) {

    const context = useContext(UserContext)
    const [divStyle, setDivStyle] = useState("")

    // Changes the component styling depending on which button was clicked
    useEffect(() => {
        if (props.buttonClicked === "map") {
            setDivStyle("destMapContainer")
            if (context.destination.length > 0) {
                initMap()
            }
        } else {
            setDivStyle("destPicContainer")
        }
    }, [props.buttonClicked])

    // Helper function that creates the dynamic Google Map centered around the destination and places a
    // marker at the destination
    function initMap() {
        // Create the map to be displayed in the popup
        const lat = context.destination[1][0]
        const lng = context.destination[1][1]
        const destLoc = new window.google.maps.LatLng(lat, lng)
        const myStyles = [
            {
                featureType: "poi",
                elementType: "labels",
                stylers: [
                    {visibility: "off"}
                ]
            }
        ]
        const map = new window.google.maps.Map(document.getElementById("destMap"), {
            zoom: 14,
            center: destLoc,
            mapTypeControl: false,
            streetViewControl: false,
            fullscreenControl: false,
            styles: myStyles
        })
        // Place marker at destination
        const marker = new window.google.maps.Marker({
            position: destLoc,
            map: map
        })
    }

    return (
        <div id="destContainer" className={divStyle}>
            <FontAwesomeIcon className="closeIcon" icon={faTimes} onClick={props.click}/>
            {props.buttonClicked === "map"
                ? context.destination.length > 0
                    ? <div id="destMap" className="destMap"/>
                    : <div className="destMapErrorContainer"><h3 className="destText">No Map Found</h3></div>
                : null
            }
            {props.buttonClicked === "pic"
                ? (context.destination.length > 0 && context.destination[2] !== "")
                    ? <img src={context.destination[2]} alt="Destination"/>
                    : <div className="destPicErrorContainer"><h3 className="destText">No Picture Found</h3></div>
                : null
            }
            <div className="destLabelContainer">Find Me!</div>
        </div>
    );
}

export default DestinationPopup;