import cntext as ct

text1 = '我好快乐'
text2='快乐'
con1=ct.sentiment(text=text1,
             diction=ct.load_pkl_dict('DUTIR.pkl')['DUTIR'],
             lang='chinese')

con2=ct.sentiment(text=text2,
             diction=ct.load_pkl_dict('DUTIR.pkl')['DUTIR'],
             lang='chinese')
print(con1)
print(con2)