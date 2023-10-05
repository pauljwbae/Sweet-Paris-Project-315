import random
import datetime
dt = datetime.datetime(2022, 1, 1)
end = datetime.datetime(2023, 8, 3)
step = datetime.timedelta(days = 1, hours = 1)
mothersDay = datetime.date(2023, 5, 12)
valentinesDay = datetime.date(2023, 2, 14)
customerUIDS = range(10000,20000)

result = []
#GENERATING DATES
while dt < end:
    #secondsStep = datetime.timedelta(seconds=random.randint(9, 59))
    
    randNumOrders = random.randint(100, 200)

    if dt.date() == mothersDay:
        randNumOrders = random.randint(200, 300)
        for i in range(randNumOrders):
            result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))

    elif dt.date() == valentinesDay:
        randNumOrders = random.randint(200, 300)
        for i in range(randNumOrders):
            result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))

    for i in range(randNumOrders):
        result.append(dt.strftime('%Y-%m-%d %H:%M:%S'))
        #dt += secondsStep
    dt += step

print(len(result))

#WRITING FILE
file = open("52weeks.csv", 'w')
#orderid(int), ordertime (datetime), customerid(int), price(double), calories(int)
file.write("OrderID,OrderDateTime,CustomerID,Price,Calories\n")
totalMoney = 0.0
orderID = 1
for date in result:
    price = round(random.uniform(5.0, 50.0),2)
    totalMoney += price
    calories = int(price * random.randint(15,40))
    #orderid(int), ordertime (datetime), customerid(int), price(double), calories(int)
    file.write(str(orderID) + "," + str(date) + "," + str(customerUIDS[random.randint(0, 9999)]) + "," + str(price) + "," +str(calories) + "\n")
    orderID += 1













#deprecated

"""
# Date,Sale,IsPeakDay
file.write("Date,Sale,IsPeakDay\n")
totalMoney = 0.0
for date in result:
    #price is expected to be 27.5
    #27.5 * 72,000 = 1,980,000
    price = round(random.uniform(5.0, 50.0),2)
    totalMoney += price



    if date[0:10] == str(valentinesDay):
        #print("valentinesDay")
        file.write(str(date)[0:10] + "," + str(price) + ",TRUE\n")
    elif date[0:10] == str(mothersDay):
        #print("mothersDay")
        file.write(str(date)[0:10] + "," + str(price) + ",TRUE\n")
    else:
        file.write(str(date)[0:10] + "," + str(price) + ",FALSE\n")
"""