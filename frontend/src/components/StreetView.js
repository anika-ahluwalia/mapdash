import React, {Component} from 'react';
import PropTypes from 'prop-types';

/*
    Component that contains the Google StreetView Panorama. Has methods that listen to position
    and POV changes.
 */
class ReactStreetview extends Component {

	constructor(props) {
		super(props);
		this.map = React.createRef()
		this.streetView = null
	}

	// Initialize the StreetViewPanorama once the window has been passed the required props
	initialize (canvas) {
		if (window.google.maps && this.streetView == null) {
			this.streetView = new window.google.maps.StreetViewPanorama(
				canvas,
				this.props.streetViewPanoramaOptions
			);

			// Set the StreetView object so GameScreen can access it
			this.props.setStreetView(this.streetView)

			this.streetView.addListener('position_changed',() => {
				
				if (this.props.onPositionChanged) {
					this.props.onPositionChanged(this.streetView.getPosition());
				}
			});

			this.streetView.addListener('pov_changed',() => {
				if (this.props.onPovChanged) {
					this.props.onPovChanged(this.streetView.getPov());
				}
			});
		}
	}

	componentDidMount () {
		this.initialize(this.map.current);
	}

	componentDidUpdate () {
		this.initialize(this.map.current);
	}
	componentWillUnmount () {
		if (this.streetView) {
			window.google.maps.event.clearInstanceListeners(this.streetView);
		}
	}

	render () {
		return <div
			ref = {this.map}
			style = {{
                flex: 1,
				height: '100%'
			}}
		></div>;
	}
}

ReactStreetview.propTypes = {
	apiKey: PropTypes.string.isRequired,
	streetViewPanoramaOptions: PropTypes.object.isRequired,
	onPositionChanged: PropTypes.func,
	onPovChanged: PropTypes.func,
};

export default ReactStreetview;