package interfaceplayground

import com.projctr.protostore.Protostore
import com.projctr.protostore.configuration.StorageConfiguration
import com.projctr.protostore.configuration.StorageConnectionConfiguration
import interfaceplayground.proto.AddressBookProtos

// making sure that the interface makes sense. Please ignore, real tests coming soon!
class InterfacePlayground {
    fun test() {
        var test: AddressBookProtos.Person = AddressBookProtos.Person.getDefaultInstance()

        var config = StorageConfiguration(StorageConnectionConfiguration("localhost", 5432, "test", "postgres", "pass", false))
        with(config.registerClass(AddressBookProtos.Person::class)) {
            tableName = "person"
            addPrimaryKey("id")
        }
        val protostore: Protostore = Protostore.create(config)

        val person = protostore.createQuery(AddressBookProtos.Person::class).getById("11").execute()
    }
}