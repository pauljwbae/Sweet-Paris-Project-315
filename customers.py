import random
import datetime

    
def gencreditcardnumber():
    cardno = "4"
    for i in range(1,16):
        if(i%4 == 0):
            cardno += " "
        cardno += str(random.randint(0,9))
    return cardno

def genphonenumber():
    phoneno = "(+1)"
    for i in range(12):
        if i == 3 or i == 7:
            phoneno += "-"
        else:
            phoneno += str(random.randint(0,9))
    return phoneno

def genbirthday():
    return datetime.datetime((1990 + random.randint(0,20)), random.randint(1,12), random.randint(1,28))

def genemail(name, bday):
    email = ""
    handles = ["@gmail.com", "@yahoo.com", "@hotmail.com", "@tamu.edu"]
    email+= name.replace(" ", "") + bday[0:4] +  handles[random.randint(0,3)]
    return email


#WRITING FILE
customerUIDS = range(10000,20000)
file_10000customers= open("10000customers.csv", 'w')
file_names = open("names.csv", 'r')

name_arr = file_names.readlines()

for i in range(len(name_arr)):
    name_arr[i] = name_arr[i][0:-2]

cnt = 0

file_10000customers.write("ID,Name,CreditCard,Email,Birthday,PhoneNumber\n")
for UID in customerUIDS:
    name = name_arr[cnt%len(name_arr)]
    bday = str(genbirthday())[0:10]
    file_10000customers.write(str(UID) + "," + name + "," + str(gencreditcardnumber()) + "," + genemail(name, bday) + "," + bday + "," + str(genphonenumber()) + "\n")
    cnt += 1

print(name_arr)
print(len(name_arr))
"""

while True:
    line = 
    if not line:
        break
    else:
        name_arr += line.strip()
    """
