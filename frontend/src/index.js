import React from 'react';
import ReactDOM from 'react-dom';
import {Beforeunload} from "react-beforeunload";
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
  <React.StrictMode>
      <Beforeunload onBeforeunload={() => "Are you sure you want to reload? You will have to create a new nickname!"}>
          <App />
      </Beforeunload>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
