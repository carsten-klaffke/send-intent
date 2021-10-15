import Foundation

public final class ShareStore {

    public static let store = ShareStore()
    private init() {
        self.title = ""
        self.description = ""
        self.type = ""
        self.url = ""
        self.processed = false
    }

    public var title: String;
    public var description: String;
    public var type: String;
    public var url: String;
    public var processed: Bool;
}
