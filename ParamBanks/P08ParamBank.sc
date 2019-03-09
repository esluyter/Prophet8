P08ParamBank {
  *new { |offset = 0|
    ^super.new.init(offset).prRegisterDependant;
  }

  *prNewArgs { |...args|
    ^super.new.init(*args).prRegisterDependant;
  }

  update { |object, param|
    this.changed(param);
  }

  prRegisterDependant {
    this.slotValuesDo { |param|
      if (param.isArray) {
        param.do(_.addDependant(this));
      } {
        param.addDependant(this);
      };
    };
  }

  doesNotUnderstand { |selector ...args|
    // allow setting of member parameters using setter notation
    // i.e. osc1.freq = 60 rather than osc1.freq.value = 60
    if (selector.isSetter) {
      var getter = selector.asGetter;
      if (this.respondsTo(getter)) {
        ^this.perform(getter).value = args[0];
      };
    };
    DoesNotUnderstandError(this, selector, args).throw;
  }

  printOn { arg stream;
		this.printClassNameOn(stream);
    stream << " ( ";
    this.slotsDo { |key, value|
      stream << key << ": " << value << ", ";
    };
    stream << " )";
	}
}
