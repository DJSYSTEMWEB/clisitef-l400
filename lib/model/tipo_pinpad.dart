library clisitef;

enum TipoPinPad {
  nenhum('NENHUM'),
  auto('ANDROID_AUTO'),
  usb('ANDROID_USB'),
  bluetooth('ANDROID_BT'),
  apos('ANDROID_APOS'),
  ingenico('ANDROID_INGENICORUA');

  const TipoPinPad(this.value);
  final String value;

  static TipoPinPad fromString(String value) {
    final tipoPinPad = TipoPinPad.values.firstWhere(
      (filter) => filter.value == value,
      orElse: () => TipoPinPad.auto,
    );

    return tipoPinPad;
  }
}
