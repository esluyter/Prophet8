Prophet08 {
  var <globals, <voice, <midiController, <ids;

  *new { |deviceName, portName, channel = 0, loadVoice = true|
    ^super.new.init(deviceName, portName, channel, loadVoice);
  }

  init { |deviceName, portName, channel, loadVoice|
    // This might not be the most elegant way of doing this...?
    P08BasicParam.resetIds;

    globals = P08Globals();
    voice = P08Voice();

    ids = P08BasicParam.ids;
    P08BasicParam.resetIds;

    globals.channel = channel;

    [globals, voice].do(_.addDependant(this));

    if (deviceName.notNil) {
      midiController = P08MidiController(this, deviceName, portName, loadVoice);
    };
  }

  at { |index|
    ^ids[index];
  }

  put { |index, value|
    ids[index].value = value;
  }

  channel {
    ^globals.channel.value;
  }

  channel_ { |value|
    globals.channel = value;
    midiController.makeDef;
  }

  update { |object, param|
    this.changed(param);
  }

  layerA { ^voice.layerA }
  layerB { ^voice.layerB }
}
