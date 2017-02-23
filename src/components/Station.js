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
    onPress: PropTypes.func
  }

  render() {
    return (
      <TouchableHighlight style={{flex: 1}} onPress={this.props.onPress}>
        {this.withImageWrapper(this.props.show.imageUrl,
          (<View style={{backgroundColor: '#ffffffdd', paddingVertical: 10, paddingHorizontal: 14}}>
            <View style={{flexDirection: 'row'}}>
              <View style={{backgroundColor: '#000', paddingHorizontal: 5, marginRight: 5}}>
                <Text style={{color: '#fff', fontWeight: 'bold', fontSize: 16}}>{this.props.name}</Text>
              </View>
              <Text style={{color: '#000', fontSize: 16}}>
                {this.props.show.title}
                {this.props.show.location && ` (${this.props.show.location})`}
              </Text>
            </View>
            {this.props.show.description &&
              <Text style={{color: '#000', marginTop: 3}}>
                {this.props.show.description}
              </Text>}
          </View>))}
      </TouchableHighlight>
    );
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
