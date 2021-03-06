/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.example.vkalko.dataprocessing.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Address extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -57670921519470654L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Address\",\"namespace\":\"org.example.vkalko.dataprocessing.model\",\"fields\":[{\"name\":\"street\",\"type\":[\"string\",\"null\"]},{\"name\":\"city\",\"type\":[\"string\",\"null\"]},{\"name\":\"zip\",\"type\":[\"string\",\"null\"]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Address> ENCODER =
      new BinaryMessageEncoder<Address>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Address> DECODER =
      new BinaryMessageDecoder<Address>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Address> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Address> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Address> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Address>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Address to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Address from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Address instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Address fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence street;
   private java.lang.CharSequence city;
   private java.lang.CharSequence zip;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Address() {}

  /**
   * All-args constructor.
   * @param street The new value for street
   * @param city The new value for city
   * @param zip The new value for zip
   */
  public Address(java.lang.CharSequence street, java.lang.CharSequence city, java.lang.CharSequence zip) {
    this.street = street;
    this.city = city;
    this.zip = zip;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return street;
    case 1: return city;
    case 2: return zip;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: street = (java.lang.CharSequence)value$; break;
    case 1: city = (java.lang.CharSequence)value$; break;
    case 2: zip = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'street' field.
   * @return The value of the 'street' field.
   */
  public java.lang.CharSequence getStreet() {
    return street;
  }


  /**
   * Sets the value of the 'street' field.
   * @param value the value to set.
   */
  public void setStreet(java.lang.CharSequence value) {
    this.street = value;
  }

  /**
   * Gets the value of the 'city' field.
   * @return The value of the 'city' field.
   */
  public java.lang.CharSequence getCity() {
    return city;
  }


  /**
   * Sets the value of the 'city' field.
   * @param value the value to set.
   */
  public void setCity(java.lang.CharSequence value) {
    this.city = value;
  }

  /**
   * Gets the value of the 'zip' field.
   * @return The value of the 'zip' field.
   */
  public java.lang.CharSequence getZip() {
    return zip;
  }


  /**
   * Sets the value of the 'zip' field.
   * @param value the value to set.
   */
  public void setZip(java.lang.CharSequence value) {
    this.zip = value;
  }

  /**
   * Creates a new Address RecordBuilder.
   * @return A new Address RecordBuilder
   */
  public static org.example.vkalko.dataprocessing.model.Address.Builder newBuilder() {
    return new org.example.vkalko.dataprocessing.model.Address.Builder();
  }

  /**
   * Creates a new Address RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Address RecordBuilder
   */
  public static org.example.vkalko.dataprocessing.model.Address.Builder newBuilder(org.example.vkalko.dataprocessing.model.Address.Builder other) {
    if (other == null) {
      return new org.example.vkalko.dataprocessing.model.Address.Builder();
    } else {
      return new org.example.vkalko.dataprocessing.model.Address.Builder(other);
    }
  }

  /**
   * Creates a new Address RecordBuilder by copying an existing Address instance.
   * @param other The existing instance to copy.
   * @return A new Address RecordBuilder
   */
  public static org.example.vkalko.dataprocessing.model.Address.Builder newBuilder(org.example.vkalko.dataprocessing.model.Address other) {
    if (other == null) {
      return new org.example.vkalko.dataprocessing.model.Address.Builder();
    } else {
      return new org.example.vkalko.dataprocessing.model.Address.Builder(other);
    }
  }

  /**
   * RecordBuilder for Address instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Address>
    implements org.apache.avro.data.RecordBuilder<Address> {

    private java.lang.CharSequence street;
    private java.lang.CharSequence city;
    private java.lang.CharSequence zip;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(org.example.vkalko.dataprocessing.model.Address.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.street)) {
        this.street = data().deepCopy(fields()[0].schema(), other.street);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.city)) {
        this.city = data().deepCopy(fields()[1].schema(), other.city);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.zip)) {
        this.zip = data().deepCopy(fields()[2].schema(), other.zip);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing Address instance
     * @param other The existing instance to copy.
     */
    private Builder(org.example.vkalko.dataprocessing.model.Address other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.street)) {
        this.street = data().deepCopy(fields()[0].schema(), other.street);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.city)) {
        this.city = data().deepCopy(fields()[1].schema(), other.city);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.zip)) {
        this.zip = data().deepCopy(fields()[2].schema(), other.zip);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'street' field.
      * @return The value.
      */
    public java.lang.CharSequence getStreet() {
      return street;
    }


    /**
      * Sets the value of the 'street' field.
      * @param value The value of 'street'.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder setStreet(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.street = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'street' field has been set.
      * @return True if the 'street' field has been set, false otherwise.
      */
    public boolean hasStreet() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'street' field.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder clearStreet() {
      street = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'city' field.
      * @return The value.
      */
    public java.lang.CharSequence getCity() {
      return city;
    }


    /**
      * Sets the value of the 'city' field.
      * @param value The value of 'city'.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder setCity(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.city = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'city' field has been set.
      * @return True if the 'city' field has been set, false otherwise.
      */
    public boolean hasCity() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'city' field.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder clearCity() {
      city = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'zip' field.
      * @return The value.
      */
    public java.lang.CharSequence getZip() {
      return zip;
    }


    /**
      * Sets the value of the 'zip' field.
      * @param value The value of 'zip'.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder setZip(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.zip = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'zip' field has been set.
      * @return True if the 'zip' field has been set, false otherwise.
      */
    public boolean hasZip() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'zip' field.
      * @return This builder.
      */
    public org.example.vkalko.dataprocessing.model.Address.Builder clearZip() {
      zip = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Address build() {
      try {
        Address record = new Address();
        record.street = fieldSetFlags()[0] ? this.street : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.city = fieldSetFlags()[1] ? this.city : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.zip = fieldSetFlags()[2] ? this.zip : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Address>
    WRITER$ = (org.apache.avro.io.DatumWriter<Address>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Address>
    READER$ = (org.apache.avro.io.DatumReader<Address>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    if (this.street == null) {
      out.writeIndex(1);
      out.writeNull();
    } else {
      out.writeIndex(0);
      out.writeString(this.street);
    }

    if (this.city == null) {
      out.writeIndex(1);
      out.writeNull();
    } else {
      out.writeIndex(0);
      out.writeString(this.city);
    }

    if (this.zip == null) {
      out.writeIndex(1);
      out.writeNull();
    } else {
      out.writeIndex(0);
      out.writeString(this.zip);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (in.readIndex() != 0) {
        in.readNull();
        this.street = null;
      } else {
        this.street = in.readString(this.street instanceof Utf8 ? (Utf8)this.street : null);
      }

      if (in.readIndex() != 0) {
        in.readNull();
        this.city = null;
      } else {
        this.city = in.readString(this.city instanceof Utf8 ? (Utf8)this.city : null);
      }

      if (in.readIndex() != 0) {
        in.readNull();
        this.zip = null;
      } else {
        this.zip = in.readString(this.zip instanceof Utf8 ? (Utf8)this.zip : null);
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (in.readIndex() != 0) {
            in.readNull();
            this.street = null;
          } else {
            this.street = in.readString(this.street instanceof Utf8 ? (Utf8)this.street : null);
          }
          break;

        case 1:
          if (in.readIndex() != 0) {
            in.readNull();
            this.city = null;
          } else {
            this.city = in.readString(this.city instanceof Utf8 ? (Utf8)this.city : null);
          }
          break;

        case 2:
          if (in.readIndex() != 0) {
            in.readNull();
            this.zip = null;
          } else {
            this.zip = in.readString(this.zip instanceof Utf8 ? (Utf8)this.zip : null);
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










