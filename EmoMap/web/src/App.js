import React, { useState, useEffect } from 'react';
import {View} from "@vkontakte/vkui";
import bridge from '@vkontakte/vk-bridge';
import '@vkontakte/vkui/dist/vkui.css';
import "./App.css";
import Start from "./panels/Start";
import Feed from "./panels/Feed";
import Map from "./panels/Map";
import Filter from "./panels/Filter";

const App = () => {
	const [activePanel, setActivePanel] = useState("start");
	const [topicId, setTopicId] = useState(0);

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
			<Feed id="feed" go={changePanel}/>
			<Map id="map" go={changePanel} setTopicId={setTopicId}/>
			<Filter id="filter" topicId={topicId}/>
		</View>
	);
}

export default App;

