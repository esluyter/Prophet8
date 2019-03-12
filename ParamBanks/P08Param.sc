P08BasicParam {
  classvar <ids;

  *initClass {
    this.resetIds;
  }

  *resetIds {
    ids = IdentityDictionary[ ];
  }

  init { |id|
    ids[id] = this;
  }
}

P08Param : P08BasicParam {
  var <id, <minValue, <maxValue, <value;

  *new { |id = 0, minValue = 0, maxValue = 127, value = 0|
    ^super.newCopyArgs(id, minValue, maxValue, value.clip(minValue, maxValue)).init(id);
  }

  value_ { |val|
    value = val.clip(minValue, maxValue);
    this.changed(this);
  }

  midiValue {
    if (minValue < 0) {
      ^(value - minValue);
    } {
      ^value;
    };
  }

  midiValue_ { |val|
    if (minValue < 0) {
      this.value_(val + minValue);
    } {
      this.value_(val);
    };
  }

  printOn { | stream |
    stream << "a P08Param  ( id: " << id << ", value: " << value << " )";
  }
}

P08Bool : P08BasicParam {
  var <id, <value;

  *new { |id = 0, value = false|
    ^super.newCopyArgs(id, value.asBoolean).init(id);
  }

  value_ { |val|
    value = val.asBoolean;
    this.changed(this);
  }

  midiValue {
    ^value.asInteger;
  }

  midiValue_ { |val|
    this.value_(val);
  }

  printOn { | stream |
    stream << "a P08Bool  ( id: " << id << ", value: " << value << " )";
  }
}
