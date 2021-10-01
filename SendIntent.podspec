
  Pod::Spec.new do |s|
    s.name = 'SendIntent'
    s.version = '0.0.1'
    s.summary = 'Plugin for Capacitor to receive send intents in Ionic'
    s.license = 'MIT'
    s.homepage = 'https://carsten-klaffke.de'
    s.author = 'Carsten Klaffke'
    s.source = { :git => 'https://github.com/carsten-klaffke/send-intent.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '12.0'
    s.dependency 'Capacitor'
  end