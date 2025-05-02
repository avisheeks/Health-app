from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from src.routers import appointment, messaging, doctors

app = FastAPI(title="Hospital Management System API")

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with specific origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(appointment.router)
app.include_router(messaging.router)
app.include_router(doctors.router)

@app.get("/")
async def root():
    return {"message": "Hospital Management System API"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True) 