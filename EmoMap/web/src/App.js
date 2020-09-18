import React, { useState, useEffect } from 'react';
import {View} from "@vkontakte/vkui";
import bridge from '@vkontakte/vk-bridge';
import '@vkontakte/vkui/dist/vkui.css';
import Start from "./panels/Start";
import "./App.css";

const App = () => {
	const [activePanel, setActivePanel] = useState("start");

	const changePanel = (p) => {
		setActivePanel(p);
		window.history.pushState({panel: p}, "");
	};

	useEffect(() => {
		window.history.pushState({panel: "start"}, "");
		window.onpopstate = function(e) {
			setActivePanel((e.state && e.state.panel) || "start")
		};

		bridge.subscribe(({ detail: { type, data }}) => {
			if (type === 'VKWebAppUpdateConfig') {
				const schemeAttribute = document.createAttribute('scheme');
				schemeAttribute.value = data.scheme ? data.scheme : 'client_light';
				document.body.attributes.setNamedItem(schemeAttribute);
			}
		});
	}, []);

	return (
		<View activePanel={activePanel}>
			<Start id="start" go={changePanel}/>
		</View>
	);
}

export default App;

