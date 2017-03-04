import React, {Component, PropTypes} from 'react';
import {View, Text, Image, TouchableHighlight} from 'react-native';

export default class Station extends Component {
  static propTypes = {
    name: PropTypes.string.isRequired,
    show: PropTypes.shape({
      title: PropTypes.string,
      description: PropTypes.string,
      location: PropTypes.string,
      url: PropTypes.string,
      imageUrl: PropTypes.string
    }),
    onPress: PropTypes.func,
    isActive: PropTypes.bool,
    playStatus: PropTypes.string
  }

  render() {
    return (
      <TouchableHighlight style={{flex: 1}} onPress={this.props.onPress}>
        {this.withImageWrapper(this.props.show.imageUrl,
          (<View style={{flexDirection: 'column'}}>
            {this.props.isActive &&
              <View style={{backgroundColor: '#000000', paddingVertical: 10, paddingHorizontal: 14}}>
                <Text style={{color: '#fff'}}>{this.friendlyStatus(this.props.playStatus)}</Text>
              </View>}
            <View style={{backgroundColor: '#ffffffdd', paddingVertical: 10, paddingHorizontal: 14}}>
              <View style={{flexDirection: 'row'}}>
                <View style={{backgroundColor: '#000', paddingHorizontal: 5, marginRight: 5, justifyContent: 'center'}}>
                  <Text style={{color: '#fff', fontWeight: 'bold', fontSize: 16}}>{this.props.name}</Text>
                </View>
                <View>
                  <Text style={{color: '#000', fontSize: 16}}>
                    {this.props.show.title}
                  </Text>
                  {this.props.show.location &&
                    <Text style={{color: '#000'}}>{this.props.show.location}</Text>}
                </View>
              </View>
              {this.props.show.description &&
                <Text style={{color: '#000', marginTop: 5}}>
                  {this.props.show.description}
                </Text>}
            </View>
          </View>))}
      </TouchableHighlight>
    );
  }

  friendlyStatus(status) {
    if (status === 'buffering') {
      return 'Loading...';
    }

    if (status === 'playing') {
      return 'Now Playing';
    }

    if (status === 'stopped') {
      return 'Stopped';
    }
  }

  withImageWrapper(imageUrl, body) {
    const style = {flex: 1, justifyContent: 'flex-end'};
    if (imageUrl) {
      return (<Image source={{uri: imageUrl}} style={{...style, resizeMode: 'cover'}}>{body}</Image>);
    }
    else {
      return (<View style={{...style, backgroundColor: '#000'}}>{body}</View>);
    }
  }
}
