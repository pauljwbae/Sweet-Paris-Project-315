import random
import datetime

dt = datetime.datetime(2023, 1, 1)
end = datetime.datetime(2023, 12, 30)

step = datetime.timedelta(days = 1)

mothersDay = datetime.date(2023, 5, 12)
valentinesDay = datetime.date(2023, 2, 14)

#print(randTime)

result = []

while dt < end:

    randNumOrders = random.randint(100, 300)

    if dt.date() == mothersDay:
        #print("before mothers day", dt.date(), len(result))
        randNumOrders = random.randint(300, 500)
        for i in range(randNumOrders):
            result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))
        #print("randNumOrders: ", randNumOrders)
        #print("after", len(result))

    elif dt.date() == valentinesDay:
        #print("before valentines day", dt.date() , len(result))
        randNumOrders = random.randint(300, 500)
        for i in range(randNumOrders):
            result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))
        #print("randNumOrders: ", randNumOrders)
        #print("after", len(result))

    for i in range(randNumOrders):
        result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))
    dt += step

#total number of days should hover around 72,000
print("\ntotal num days: ", len(result))

file = open("52weeks.csv", 'w')

# Date,Sale,IsPeakDay
file.write("Date,Sale,IsPeakDay\n")
for date in result:
    #price is expected to be 27.5
    #27.5 * 72,000 = 1,980,000
    price = random.uniform(5.0, 50.0)
    price = round(price, 2)

    if date[0:10] == str(valentinesDay):
        #print("valentinesDay")
        file.write(str(date)[0:10] + "," + str(price) + ",TRUE\n")
    elif date[0:10] == str(mothersDay):
        #print("mothersDay")
        file.write(str(date)[0:10] + "," + str(price) + ",TRUE\n")
    else:
        file.write(str(date)[0:10] + "," + str(price) + ",FALSE\n")
