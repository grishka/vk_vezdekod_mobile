import React,{useState, useEffect} from 'react';
import {Button, Panel, PanelHeader} from "@vkontakte/vkui";
const mapboxgl = require('mapbox-gl/dist/mapbox-gl.js');

const Map = ({id, go, setTopicId}) => {
    const lastChecked = {t: 0};
    const checkInterval = 100;
    const [map, setMap] = useState(null);
    const [lTime, setLTime] = useState(0);

    useEffect(() => {
        mapboxgl.accessToken = 'pk.eyJ1IjoiZGVuaXNucCIsImEiOiJja2Y4eTAxM3MwMWR6MnhvODFmc3o2ZXVhIn0.w1cS5wtN6TfXPLNBAqRsPw';
        const m = new mapboxgl.Map({
            container: 'mapInside',
            style: 'mapbox://styles/mapbox/light-v10',
            center: [30.31025, 59.937],
            zoom: 9.89,
        });

        m.on('load', () => {
            m.getStyle().layers.forEach((thisLayer) => {
                if(thisLayer.id.includes('-label')){
                    m.setLayoutProperty(thisLayer.id, 'text-field', ['get','name_ru'])
                }
            });
            onMove();
        });

        m.on('move', onMove);
        m.on('moveend', onMove);

        setMap(m);
    }, []);

    useEffect(() => {
        //redraw
    }, [lTime])

    const onMove = () => {
        const t = (new Date()).getTime();
        const diff = t - lastChecked.t;
        if (diff > checkInterval) {
            lastChecked.t = t;
            setLTime(t);
        }
    };

    const goNext = (topicId) => {
        setTopicId(topicId);
        go("filter");
    };

    return (
        <Panel id={id}>
            <div id="mapInside"/>
        </Panel>
    );
}

export default Map;
