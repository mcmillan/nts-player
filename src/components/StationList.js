import React, {Component} from 'react';
import {View, Text, Alert, NativeModules, DeviceEventEmitter} from 'react-native';
import Station from './Station';
import Loading from './Loading';

export default class StationList extends Component {
  constructor() {
    super()
    this.state = {
      stations: [],
      currentUrl: null,
      playStatus: 'stopped',
      errored: false
    };
  }

  componentDidMount() {
    this.loadStations();
  }

  componentWillMount() {
    DeviceEventEmitter.addListener(
      'playbackStatusUpdate',
      this.statusDidUpdate.bind(this)
    );
    NativeModules.Streaming.refresh();
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
        {this.state.errored && <Text>Error</Text>}
        {this.state.stations.length == 0 && <Loading />}
        {this.state.stations.length > 0 &&
          this.state.stations.map((s) =>
            <Station
              {...s}
              onPress={() => this.playOrStop(s.streamUrl)}
              key={s.name}
              isActive={s.streamUrl == this.state.currentUrl}
              playStatus={this.state.playStatus}
            />)}
      </View>
    );
  }

  playOrStop(url) {
    if (this.state.playStatus !== 'stopped' && url === this.state.currentUrl) {
      return this.stop();
    }
    NativeModules.Streaming.play(url);
  }

  stop() {
    NativeModules.Streaming.stop();
  }

  statusDidUpdate(status) {
    this.setState(status);
  }
}
