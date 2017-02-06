package com.redhat.xpaas.datagrid.hotrod.query.marshallers;


import org.infinispan.protostream.MessageMarshaller;

import com.redhat.xpaas.datagrid.hotrod.query.domain.Person;

import java.io.IOException;

public class PersonMarshaller implements MessageMarshaller<Person> {

	@Override
	public String getTypeName() {
		return "xpaas.Person";
	}

	@Override
	public Class<Person> getJavaClass() {
		return Person.class;
	}

	@Override
	public Person readFrom(ProtoStreamReader reader) throws IOException {
		String name = reader.readString("name");
		int id = reader.readInt("id");

		Person person = new Person();
		person.setName(name);
		person.setId(id);
		return person;
	}

	@Override
	public void writeTo(ProtoStreamWriter writer, Person person) throws IOException {
		writer.writeString("name", person.getName());
		writer.writeInt("id", person.getId());
	}

}