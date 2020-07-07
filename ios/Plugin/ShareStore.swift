import Foundation

final class ShareStore {
    
    static let store = ShareStore()
    private init() {}

    var text: String;
    var URL: String;
    var image: String;
}
