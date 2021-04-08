import discord
from kafka import KafkaConsumer
from threading import Thread, Event
import json

thread = Thread()
thread_stop_event = Event()

client = discord.Client()

@client.event
async def on_ready():
    client.loop.create_task(consumer())
    print("We have logged in as {0.user}".format(client))

async def consumer():
    await client.wait_until_ready()
    channel = client.get_channel(823820979137675335)
    consumer = KafkaConsumer('alerts',
                         group_id='my_group5',
                         bootstrap_servers=['localhost:9092'])
    while not thread_stop_event.isSet():
        for message in consumer:
            print ("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                          message.offset, message.key,
                                          message.value))
            print(message.value)
            m = json.loads(message.value)
            track = "https://www.google.com/maps/search/?api=1&query="+str(m['Latitude'])+","+str(m['Longitude'])
            string_m = ":hear_no_evil: :rotating_light: **ALERT CITIZEN** :rotating_light: :hear_no_evil:\n\n**Drone ID**: "+str(m['DroneID'])+"/ **Record**: "+str(m['Record'])+"\n**Battery**: "+ str(m['Battery'])+"%\n**Citizen**: "+str(m['Citizen'])+"\n**Message**: "+str(m['Message'])+"\n**PeaceScore**: "+str(m['PeaceScore'])+"\n**Country**: "+str(m['Country'])+"\n**City**: "+str(m['City'])+"\n\n:exclamation:**TRACK THE CITIZEN**:exclamation: :map:\n "+str(track)  
            await channel.send(string_m)

@client.event
async def on_message(message):
    if message.author == client.user:
        return
    
    if message.content.startswith("$hello"):
        await message.channel.send("Hello!")

    if message.content == "Ping":
        await message.channel.send("Pong")

client.run("ODIzNDU5ODI3OTI1MjU0MjA1.YFhIug.tr_Ife8OkrEOtZu1HqBBGxcMraA")
