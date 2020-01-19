#include "PinChangeInt.h" // PinChange Interrupt library
#include <AESLib.h>
#include <SPI.h>
#include <LoRa.h>
#include <string.h>

#define DEVICE_ID "uigen"
#define PROJECT_ID 1

const byte ledPinGreen = 7;
const byte ledPinRed = 6;

const byte interruptPinRed = 4;
const byte interruptPinGreen = 3;

const long debouncing_time = 50; //Debouncing Time in Milliseconds
const long led_time = 3000; //LED Time in Milliseconds

volatile unsigned long last_micros;
volatile unsigned long green_micros;

volatile unsigned long last_microsRed;
volatile unsigned long red_micros;

//MQTT
const long interval = 60000 * 10; //Interval is the time a packet waits before being send (60 000 = 1 min || 10 = 10 min)
unsigned long previousMillis = 0; // Tracks the time since last event fired

int count; //Counts messages in the storage. Is set to 0 when the packet has been sent. 

const int projectIdDigits = 4;
const int maxvotes = 10;

char buffer[projectIdDigits + maxvotes +1] = {0}; //Storage for messages

int statusGreen; //Used to see is a button is clicked or released. Main reason: No double vote when user kept button pressed during the wait time.
int statusRed; //Used to see is a button is clicked or released. Main reason: No double vote when user kept button pressed during the wait time.

//ENCRYPTION
const byte MIN_ASCII = 33;
const byte MAX_ASCII = 126;

void setup() {
  pinMode(ledPinGreen, OUTPUT);
  pinMode(ledPinRed, OUTPUT);
  
  pinMode(interruptPinGreen, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(interruptPinGreen), green, CHANGE);
  attachPinChangeInterrupt(interruptPinRed, red, CHANGE); 

  memset(buffer, 0, sizeof(buffer)); //Initialize empty buffer
  strcpy(buffer, PROJECT_ID); //Copy PROJECT_ID in buffer
  
  Serial.begin(9600);

  for(int i=projectIdDigits; i > 0; i--)
  {
    char st[1];
    itoa((PROJECT_ID/(int)pow(10, i-1))%10, st, 10);
    strcat(buffer, st);
  }


  Serial.println("Setup LoRa shield...");
  if (!LoRa.begin(868100000)) 
  {
    Serial.println("Starting LoRa failed!");
    while(1);
  }
    LoRa.setSyncWord(0x34);
    Serial.println("LoRa shield initialized");
    Serial.println("Signalling ready by flashing LED 3 times");
    for(int i=0; i<3; i++) 
    {
      digitalWrite(ledPinGreen, HIGH);
      digitalWrite(ledPinRed, HIGH);
      delay(200);
      digitalWrite(ledPinGreen, LOW);
      digitalWrite(ledPinRed, LOW);
      delay(400);
    }
    Serial.println("Ready!");
}

void loop() {
    if((long)(micros() - green_micros) >= led_time * 1000) 
    {
      digitalWrite(ledPinGreen, LOW);  // turn Green LED OFF
    }
  
    if((long)(micros() - red_micros) >= led_time * 1000) 
    {
      digitalWrite(ledPinRed, LOW);  // turn Red LED OFF
    }

  if (count >= 10) 
  {
    //Serial.println("Queue is full");
    count=0;
    send();
    //previousMillis = millis(); //Reset time since last send. 
  }

  if ((unsigned long)(millis() - previousMillis) >= interval && count > 0) 
  {
    Serial.println("Queue not full, wait time passed");
    send();
    //previousMillis = millis(); //Reset time since last send.
    count=0;
  }
}

void green() {
  if((long)(micros() - last_micros) >= debouncing_time * 1000) {
    //Serial.println("Interrupt GREEN");
    if (statusGreen == 1){
      //Serial.println("released");
      if((long)(micros() - red_micros) >= led_time * 1000 && (long)(micros() - green_micros) >= led_time * 1000) 
      {
        digitalWrite(ledPinGreen, HIGH);  // turn Green LED ONN
        Serial.println("VOTED GREEN");
        strcat(buffer, "+");
        Serial.println(buffer);
        if (count == 0) //Resets timer to the time the first messages is added to the queue.
        { 
          previousMillis = millis();
        }
        count++;
            
        green_micros = micros(); 
      }
      statusGreen=0;
    }
    else 
    {
      //Serial.println("clicked");
      statusGreen=1;
    } 
    
  last_micros = micros();
  }
}

void red() {
  if((long)(micros() - last_microsRed) >= debouncing_time * 1000) 
  {
    //Serial.println("interruptRED");

    if (statusRed == 1)
    {
      //Serial.println("released");
        if((long)(micros() - red_micros) >= led_time * 1000 && (long)(micros() - green_micros) >= led_time * 1000) 
        {
          digitalWrite(ledPinRed, HIGH);  // turn Red LED ONN
          Serial.println("VOTED RED"); 
          strcat(buffer, "-");
          Serial.println(buffer);
          if (count == 0) //Resets timer to the time the first messages is added to the queue.
          { 
            previousMillis = millis();
          }
          count++;
          
          red_micros = micros();
        }
    statusRed=0;
    }
    else 
    {
      //Serial.println("clicked");
      statusRed=1;
    }       
    last_microsRed = micros();
  }
}

void rot47()
{
  char charArray[sizeof(buffer) / sizeof(buffer[0])];
  strcpy(charArray, buffer);
 
  for (int i = 0; i < sizeof(buffer) / sizeof(buffer[0]); i++)
  {
    int asciiVal = charArray[i];
 
    if (charArray[i] >= MIN_ASCII && charArray[i] <= MAX_ASCII)
    {
      if (asciiVal > (MAX_ASCII - 47))
      {
        asciiVal -= 47;
      }
      else
      {
        asciiVal += 47;
      }
    }
 
    charArray[i] = (char)asciiVal;
  }
 
  strcpy(buffer, charArray);
  Serial.println("Encrypted: ");
  Serial.println(buffer);        
}

void cleanbuffer() //Resets buffer to all zero's with at place 1 the project id. 
{
  Serial.println("Clean buffer and initialize PROJECT_ID");
  memset(buffer, 0, strlen(buffer));
  
  for(int i=projectIdDigits; i > 0; i--)
  {
    char st[1];
    itoa((PROJECT_ID/(int)pow(10, i-1))%10, st, 10);
    strcat(buffer, st);
  }
}

void send() 
{
  rot47(); //Encrypt buffer before sending.
  Serial.print("Sending packet ");
  Serial.println("... ");
  int status = LoRa.beginPacket();
  if (status) 
  {
    LoRa.print("<"); //Print device ID
    LoRa.print(DEVICE_ID);
    LoRa.print(">");

    for(int i=0; i< (sizeof(buffer) / sizeof(buffer[0])) -1; i++){
      char myChar = buffer[i];
      
      for(int i=7; i>=0; i--)
      {  
        byte bytes = bitRead(myChar,i);
        Serial.print(bytes, BIN);
        LoRa.print(bytes, BIN); //Print each character in bytes.
      }
    }
    
    LoRa.endPacket();
    Serial.println("");
    Serial.println("Packet sent");
  } 
  else 
  {
    Serial.println("Error sending packet");
  }
  cleanbuffer(); //Resets buffer to all zero's with at place 1 the project id.
}

//AES ENCRYPTION --> OPTIONAL
/*
void encrypt(char buffer[]) {
  Serial.println("Encrypt");
  //Serial.begin(57600);
  byte key[] = "0001001010101010";
  char data[] = "00010010101110"; //16 chars == 16 bytes
  aes128_enc_single(key, data);
  Serial.print("encrypted:");
  strcpy(buffer, data);
  Serial.println(data);
  aes128_dec_single(key, data);
  Serial.print("decrypted:");
  Serial.println(data);
}
*/
