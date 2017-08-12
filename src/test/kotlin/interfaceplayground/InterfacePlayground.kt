package interfaceplayground

import com.projctr.protostorage.Protostorage
import com.projctr.protostorage.configuration.StorageConfiguration
import com.projctr.protostorage.configuration.StorageConnectionConfiguration
import interfaceplayground.proto.AddressBookProtos

// making sure that the interface makes sense. Please ignore, real tests coming soon!
fun main(args : Array<String>) {
    var config = StorageConfiguration(StorageConnectionConfiguration("localhost", 5432, "test", "postgres", "pass", false))
    with(config.registerClass(AddressBookProtos.Person::class)) {
        tableName = "person"
        addPrimaryKey("id")
    }
    val protostorage: Protostorage = Protostorage.create(config)

    val personToInsert = AddressBookProtos.Person.newBuilder()
    personToInsert.id = 11
    personToInsert.name = "Rémi"
    personToInsert.email = "remi@procjtr.com"

    protostorage.insert(personToInsert.build())

    val person = protostorage.createQuery(AddressBookProtos.Person::class).getById("11").execute()
}