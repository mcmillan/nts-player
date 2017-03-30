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
      currentNotificationTitle: null,
      currentNotificationText: null,
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
    this.updateNowPlaying();
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
              onPress={() => this.playOrStop(s)}
              key={s.name}
              isActive={s.streamUrl == this.state.currentUrl}
              playStatus={this.state.playStatus}
            />)}
      </View>
    );
  }

  updateNowPlaying() {
    if (this.state.playStatus !== 'playing') {
      return;
    }

    const station = this.state.stations.find((s) => s.streamUrl === this.state.currentUrl);

    if (!station) {
      return;
    }

    NativeModules.Streaming.updateNowPlaying(station.show.title, station.show.description);
  }

  playOrStop(station) {
    if (this.state.playStatus !== 'stopped' && station.streamUrl === this.state.currentUrl) {
      return this.stop();
    }
    NativeModules.Streaming.play(
      station.streamUrl,
      station.show.title,
      station.show.description
    );
  }

  stop() {
    NativeModules.Streaming.stop();
  }

  statusDidUpdate(status) {
    this.setState(status);
  }
}
