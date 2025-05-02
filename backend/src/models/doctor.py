from pydantic import BaseModel

class Doctor(BaseModel):
    id: str
    name: str
    specialty: str
    latitude: float
    longitude: float 