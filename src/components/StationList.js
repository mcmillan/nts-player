import React, {Component} from 'react';
import {View, Alert, NativeModules} from 'react-native';
import Station from './Station';

export default class StationList extends Component {
  constructor() {
    super()
    this.state = {
      stations: []
    };
  }

  componentDidMount() {
    this.loadStations();
  }

  async loadStations() {
    const response = await fetch('https://nts-api.joshmcmillan.com/api/live')
    const responseJSON = await response.json();
    this.setState({stations: responseJSON.stations});
    setTimeout(() => this.loadStations(), 1000 * 10);
  }

  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', alignItems: 'stretch', justifyContent: 'center'}}>
        {this.state.stations.map((s) =>
          <Station {...s} onPress={() => this.play(s.streamUrl)} key={s.name} />)}
      </View>
    );
  }

  play(url) {
    NativeModules.Streaming.play(url);
  }
}
