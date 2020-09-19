import React,{useState, useEffect} from 'react';
import {Button, Panel, PanelHeader, Search} from "@vkontakte/vkui";
import Topic from "../components/Topic";
import {emotions, topics} from "../state";
const mapboxgl = require('mapbox-gl/dist/mapbox-gl.js');

const Map = ({id, go, setTopicId}) => {
    const lastChecked = {t: 0};
    const checkInterval = 100;
    const [map, setMap] = useState(null);
    const [lTime, setLTime] = useState(0);
    const [search, setSearch] = useState("");
    const [filteredTopics, setFilteredTopics] = useState([...topics]);

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

            // create markers
            topics.forEach((t, i) => {
                const el = document.createElement('div');
                el.className = 'topic-marker';
                el.id = 'topic-marker-' + i;
                const w = t.radius;
                el.innerHTML = `<div class="topic-marker" style="width: ${w}px; height: ${w}px;">${t.icon}</div>`;

                el.addEventListener('click', () => {
                    goNext(i);
                });

                const marker = new mapboxgl.Marker(el)
                    .setLngLat(new mapboxgl.LngLat(t.lng, t.lat))
                    .addTo(m);
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

    useEffect(() => {
        if (!search) {
            setFilteredTopics([...topics]);
        } else {
            const fTopics = topics.filter((t) => {
                const e = emotions[t.emotion];
                return t.name.toLowerCase().includes(search.toLowerCase())
                    || e.search.some((s) => search.toLowerCase().includes(s) || s.includes(search.toLowerCase()));
            });
            setFilteredTopics(fTopics);
        }
    }, [search]);

    const goTopic = (t) => {
        map.flyTo({
            center: [t.lng, t.lat],
            zoom: 10,
        });
    };

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
            <div className="map-panel">
                <Search value={search} onChange={(e) => setSearch(e.currentTarget.value)}/>
                <div className="topics-line">
                    {filteredTopics.map((t, i) => <Topic click={() => goTopic(t)} topic={t} key={t.name}/>)}
                    <div style={{height: 1, width: 12, minWidth: 12}}/>
                </div>
            </div>
        </Panel>
    );
}

export default Map;
