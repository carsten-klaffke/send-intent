import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import * as serviceWorker from './serviceWorker';

import {registerWebPlugin} from "@capacitor/core";
import {SendIntent} from "send-intent";

ReactDOM.render(<App />, document.getElementById('root'));

registerWebPlugin(SendIntent);
// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
