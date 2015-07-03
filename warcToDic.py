import sys,tarfile,os,traceback
import warc


def main():
    current_wkd = os.getcwd()
    dic = readFolder(current_wkd)
    
def readFolder(folder):
    arcs = []    

    for dirpath, dir, files in os.walk(folder):
        for file in files:
        if file.endswith('.warc'):
            arc = convertArcToDict(file)
            arcs.append(arc)
    return arcs

def convertArcToDict(arcfile):
    arc = {}
    try:
        record = warc.open(arcfile)
        for data in record:
            if not ("jpg" in data.header.url or "png" in data.header.url or "css" in data.header.url):
                arc[data.header.url] = {
                    "header":data.header,
                    "raw": data.payload
                }
    except Exception as e:
        print "Error in reading arc file: %s " 
    return arc

if __name__ == "__main__":
    main()